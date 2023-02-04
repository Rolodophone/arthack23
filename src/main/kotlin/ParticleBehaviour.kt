import org.openrndr.Program
import org.openrndr.extra.noise.gradient3D
import org.openrndr.extra.noise.simplex4D

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

		val avgPos = MutableVector()
		val particleGrid = Array(GRID_TOTAL_HEIGHT) { Array(GRID_TOTAL_WIDTH) { mutableListOf<Particle>() } }

		override fun update(particles: List<Particle>) {
			//update average
			avgPos.set(particles.fold(MutableVector(0.0, 0.0)) { acc, particle -> acc + particle.pos }
					/ particles.size.toDouble())

			//update grid
			particleGrid.forEach { it.forEach { it.clear() } }
			for (particle in particles) {
				val gridX = (particle.pos.x / program.width * GRID_ONSCREEN_WIDTH + GRID_OFFSCREEN_MARGIN).toInt()
				val gridY = (particle.pos.y / program.height * GRID_ONSCREEN_HEIGHT + GRID_OFFSCREEN_MARGIN).toInt()
				if (gridX < 0 || gridX >= GRID_ONSCREEN_WIDTH || gridY < 0 || gridY >= GRID_ONSCREEN_HEIGHT) continue
				particleGrid[gridY][gridX].add(particle)
			}
		}

		override fun updateParticle(particle: Particle) {
			//friction
			particle.vel.scale(0.997)

			//acceleration from simplex noise
			particle.vel.add(MutableVector(gradient3D(
				noise = simplex4D,
				seed = 0,
				x = 0.005*particle.pos.x,
				y = 0.005*particle.pos.y,
				z = 0.005*program.frameCount.toDouble()
			).xy) * 0.01)

			//avoid going offscreen
			when {
				particle.pos.x > program.width - GRID_SQUARE_SIZE*GRID_OFFSCREEN_MARGIN
					-> particle.vel.x -= 0.05
				particle.pos.x < GRID_SQUARE_SIZE*GRID_OFFSCREEN_MARGIN
					-> particle.vel.x += 0.05
				particle.pos.y > program.height - GRID_SQUARE_SIZE*GRID_OFFSCREEN_MARGIN
					-> particle.vel.y -= 0.05
				particle.pos.y < GRID_SQUARE_SIZE*GRID_OFFSCREEN_MARGIN
					-> particle.vel.y += 0.05
			}

			//bounce back to the screen
			when {
				particle.pos.x > program.width + GRID_SQUARE_SIZE*GRID_OFFSCREEN_MARGIN
				|| particle.pos.x < -GRID_SQUARE_SIZE*GRID_OFFSCREEN_MARGIN
					-> particle.vel.x = -particle.vel.x
				particle.pos.y > program.height + GRID_SQUARE_SIZE*GRID_OFFSCREEN_MARGIN
				|| particle.pos.y < -GRID_SQUARE_SIZE*GRID_OFFSCREEN_MARGIN
					-> particle.vel.y = -particle.vel.y
			}

			//gravity
			particle.vel.add((avgPos - particle.pos) * 0.00002)

			//repulsion from nearby particles
			val gridX = (particle.pos.x / program.width * GRID_ONSCREEN_WIDTH + GRID_OFFSCREEN_MARGIN).toInt()
			val gridY = (particle.pos.y / program.height * GRID_ONSCREEN_HEIGHT + GRID_OFFSCREEN_MARGIN).toInt()
			if (gridX in 0 until GRID_ONSCREEN_WIDTH && gridY in 0 until GRID_ONSCREEN_HEIGHT) {
				for (nearbyParticle in particleGrid[gridY][gridX]) {
					val s = nearbyParticle.pos - particle.pos
					particle.vel.add(s * -0.0001)
				}
			}

			particle.pos.add(particle.vel * 1.0)
		}
	}
}
