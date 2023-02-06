import org.openrndr.Program

class Nebula(private val program: Program) {
	val particles = mutableListOf<Particle>()
	var particleBehaviour = ParticleBehaviour.Murmur(program)
	var frameNumber = 0

	fun setup() {
		particleBehaviour.apply {
			simplexSeed = 0
			friction = 1.0
			simplexScale = 0.01
			simplexSpeed = 0.01
			simplexWeight = 0.001
			repulsionWeight = 0.0
			gravityWeight = 0.0002
		}
	}

	fun update() {
		when (frameNumber) {
			in 0..999 -> {
				for (i in 0..19) {
					particles.add(Particle(500.0, program.height / 2.0 - 40 + 4*i).apply {
						vel.set(4.0, 4 + i/16.0)
					})
				}
				for (i in 0..19) {
					particles.add(Particle(program.width - 500.0, program.height / 2.0 - 40 + 4*i).apply {
						vel.set(-4.0, -4 - i/16.0)
					})
				}
			}
		}

		particleBehaviour.update(particles)
		particles.forEach { particleBehaviour.updateParticle(it, frameNumber) }

		frameNumber++
	}

	fun draw() {
		program.drawer.points {
			particles.forEach { it.draw(this) }
		}
	}
}