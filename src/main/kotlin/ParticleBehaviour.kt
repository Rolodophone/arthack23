import org.openrndr.Program
import org.openrndr.extra.noise.gradient3D
import org.openrndr.extra.noise.simplex4D
import kotlin.math.absoluteValue

sealed class ParticleBehaviour {
	open fun update(particles: List<Particle>) {}
	open fun updateParticle(particle: Particle) {}

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

		var friction = 0.997
		var simplexSeed = 0
		var simplexScale = 0.005
		var simplexSpeed = 0.005
		var simplexWeight = 0.01
		var nearScreenEdgeAccel = 0.05
		var gravityWeight = 0.00002
		var repulsionWeight = 0.0001
		var totalVelWeight = 1.0

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

		override fun updateParticle(particle: Particle) {
			//friction
			particle.vel.scale(friction)

			//acceleration from simplex noise
			particle.vel.add(MutableVector(gradient3D(
				noise = simplex4D,
				seed = simplexSeed,
				x = simplexScale*particle.pos.x,
				y = simplexScale*particle.pos.y,
				z = simplexSpeed*program.frameCount.toDouble()
			).xy) * simplexWeight)

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
			val gridX = (particle.pos.x / program.width * GRID_ONSCREEN_WIDTH + GRID_OFFSCREEN_MARGIN).toInt()
			val gridY = (particle.pos.y / program.height * GRID_ONSCREEN_HEIGHT + GRID_OFFSCREEN_MARGIN).toInt()
			if (gridX in 0 until GRID_TOTAL_WIDTH && gridY in 0 until GRID_TOTAL_HEIGHT) {
				for (nearbyParticle in particleGrid[gridY][gridX]) {
					val s = nearbyParticle.pos - particle.pos
					particle.vel.add(s * -repulsionWeight)
				}
			}

			particle.pos.add(particle.vel * totalVelWeight)
		}
	}

	class TempLine(private val program: Program) : ParticleBehaviour() {
		override fun updateParticle(particle: Particle) {
			if (particle.pos.x < program.width / 2) {
				particle.vel.x += 0.1
			} else {
				particle.vel.x -= 0.1
			}

			particle.pos.add(particle.vel)
		}
	}
}
