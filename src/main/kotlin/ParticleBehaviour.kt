import org.openrndr.Program
import org.openrndr.extra.noise.gradient3D
import org.openrndr.extra.noise.simplex4D
import org.openrndr.shape.Circle
import org.openrndr.shape.ShapeContour
import kotlin.math.absoluteValue

sealed class ParticleBehaviour {
	open fun update(particles: List<Particle>) {}
	open fun updateParticle(particle: Particle, frameNumber: Int) {}

	class Murmur(private val program: Program) : ParticleBehaviour() {
		companion object {
			const val GRID_SQUARE_SIZE = 30
			const val GRID_OFFSCREEN_MARGIN = 2
			const val GRID_ONSCREEN_WIDTH = 1920 / GRID_SQUARE_SIZE
			const val GRID_ONSCREEN_HEIGHT = 1080 / GRID_SQUARE_SIZE
			const val GRID_TOTAL_WIDTH = GRID_ONSCREEN_WIDTH + GRID_OFFSCREEN_MARGIN * 2
			const val GRID_TOTAL_HEIGHT = GRID_ONSCREEN_HEIGHT + GRID_OFFSCREEN_MARGIN * 2
		}

		private val avgPos = MutableVector()
		private val particleGrid = Array(GRID_TOTAL_HEIGHT) { Array(GRID_TOTAL_WIDTH) { mutableListOf<Particle>() } }

		var friction = 0.8
		var simplexSeed = 0
		var simplexScale = 0.005
		var simplexSpeed = 0.005
		var simplexWeight = 0.01
		var nearScreenEdgeAccel = 0.05
		var gravityWeight = 0.00002
		var repulsionWeight = 0.0001
		var contourAttraction = 0.01
		var contourAccel = 0.2
		var totalVelWeight = 1.0

		var contour: ShapeContour? = Circle(program.width / 2.0, program.height / 2.0, 300.0).contour

		override fun update(particles: List<Particle>) {
			//update average
			avgPos.set(particles.fold(MutableVector(0.0, 0.0)) { acc, particle -> acc + particle.pos }
					/ particles.size.toDouble())

			//update grid
			particleGrid.forEach { it.forEach { it.clear() } }
			for (particle in particles) {
				val gridX = (particle.pos.x / program.width * GRID_ONSCREEN_WIDTH + GRID_OFFSCREEN_MARGIN).toInt()
				val gridY = (particle.pos.y / program.height * GRID_ONSCREEN_HEIGHT + GRID_OFFSCREEN_MARGIN).toInt()
				if (gridX < 0 || gridX >= GRID_TOTAL_WIDTH || gridY < 0 || gridY >= GRID_TOTAL_HEIGHT) continue
				particleGrid[gridY][gridX].add(particle)
			}
		}

		override fun updateParticle(particle: Particle, frameNumber: Int) {
			//friction
			particle.vel.scale(friction)

			//acceleration from simplex noise
			if (simplexWeight != 0.0) {
				particle.vel.add(MutableVector(gradient3D(
					noise = simplex4D,
					seed = simplexSeed,
					x = simplexScale * particle.pos.x,
					y = simplexScale * particle.pos.y,
					z = simplexSpeed * frameNumber.toDouble()
				).xy) * simplexWeight)
			}

			//avoid going offscreen
			when {
				particle.pos.x > program.width - GRID_SQUARE_SIZE*GRID_OFFSCREEN_MARGIN
					-> particle.vel.x -= nearScreenEdgeAccel
				particle.pos.x < GRID_SQUARE_SIZE*GRID_OFFSCREEN_MARGIN
					-> particle.vel.x += nearScreenEdgeAccel
				particle.pos.y > program.height - GRID_SQUARE_SIZE*GRID_OFFSCREEN_MARGIN
					-> particle.vel.y -= nearScreenEdgeAccel
				particle.pos.y < GRID_SQUARE_SIZE*GRID_OFFSCREEN_MARGIN
					-> particle.vel.y += nearScreenEdgeAccel
			}

			//bounce back to the screen
			when {
				particle.pos.x > program.width + GRID_SQUARE_SIZE*GRID_OFFSCREEN_MARGIN
					-> particle.vel.x = -particle.vel.x.absoluteValue
				particle.pos.x < -GRID_SQUARE_SIZE*GRID_OFFSCREEN_MARGIN
					-> particle.vel.x = particle.vel.x.absoluteValue
				particle.pos.y > program.height + GRID_SQUARE_SIZE*GRID_OFFSCREEN_MARGIN
					-> particle.vel.y = -particle.vel.y.absoluteValue
				particle.pos.y < -GRID_SQUARE_SIZE*GRID_OFFSCREEN_MARGIN
					-> particle.vel.y = particle.vel.y.absoluteValue
			}

			//gravity
			particle.vel.add((avgPos - particle.pos) * gravityWeight)

			//repulsion from nearby particles
			if (repulsionWeight != 0.0) {
				val gridX = (particle.pos.x / program.width * GRID_ONSCREEN_WIDTH + GRID_OFFSCREEN_MARGIN).toInt()
				val gridY = (particle.pos.y / program.height * GRID_ONSCREEN_HEIGHT + GRID_OFFSCREEN_MARGIN).toInt()
				if (gridX in 0 until GRID_TOTAL_WIDTH && gridY in 0 until GRID_TOTAL_HEIGHT) {
					for (nearbyParticle in particleGrid[gridY][gridX]) {
						val s = nearbyParticle.pos - particle.pos
						particle.vel.add(s * -repulsionWeight)
					}
				}
			}

			contour?.let { contour ->
				//attraction to contour
				if (contourAttraction != 0.0) {
					val s = contour.nearestPatch(particle.pos.toVector2()).position - particle.pos
					particle.vel.add(s * contourAttraction)
				}

				//acceleration from contour
				if (contourAccel != 0.0) {
					val nearestPoint = contour.nearest(particle.pos.toVector2())
					val v = nearestPoint.segment.derivative(nearestPoint.segmentT).normalized
					particle.vel.add(v * contourAccel)
				}
			}

			particle.pos.add(particle.vel * totalVelWeight)
		}
	}
}
