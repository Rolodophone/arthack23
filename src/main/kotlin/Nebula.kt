import org.openrndr.Program

class Nebula(private val program: Program) {
	private val particles = mutableListOf<Particle>()
	private var particleBehaviour = ParticleBehaviour.Murmur(program)

	fun setup() {
		particleBehaviour.apply {
			simplexSeed = 0
			friction = 0.997
			simplexScale = 0.01
			simplexSpeed = 0.01
			simplexWeight = 0.001
			repulsionWeight = 0.0
			gravityWeight = 0.0002
		}
	}

	fun update() {
		when (program.frameCount) {
			in 0..1999 -> {
				for (i in 0..19) {
					particles.add(Particle(0.0, program.height / 2.0 - 40 + 4*i).apply {
						vel.set(3.0, i/4.0)
					})
				}
				for (i in 0..19) {
					particles.add(Particle(program.width.toDouble(), program.height / 2.0 - 40 + 4*i).apply {
						vel.set(-3.0, -i/4.0)
					})
				}
			}
		}

		particleBehaviour.update(particles)
		particles.forEach { particleBehaviour.updateParticle(it) }
	}

	fun draw() {
		program.drawer.points {
			particles.forEach { it.draw(this) }
		}
	}
}