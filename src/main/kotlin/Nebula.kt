import org.openrndr.Program
import org.openrndr.extra.noise.Random

class Nebula(private val program: Program) {
	private val particles = mutableListOf<Particle>()
	private var particleBehaviour: ParticleBehaviour = ParticleBehaviour.Murmur(program)

	fun setup() {
	}

	fun update() {
		when (program.frameCount) {
			in 0..399 -> {
				repeat(10) {
					val variance = Random.double()
					val newParticle = Particle(0.0, program.height/2.0 + 200*variance)
					newParticle.vel.set(3.0, 3*variance)
					particles.add(newParticle)
				}
				repeat(10) {
					val variance = Random.double()
					val newParticle = Particle(program.width.toDouble(),
							program.height/2.0 + 200*variance)
					newParticle.vel.set(-3.0, 3*variance)
					particles.add(newParticle)
				}
			}
			500 -> { //tube
				particleBehaviour = ParticleBehaviour.TempLine(program)
			}
			1000 -> { //random noise
				particleBehaviour = ParticleBehaviour.Murmur(program).apply {
					repulsionWeight = 0.01
				}
			}
			1500 -> {
				(particleBehaviour as ParticleBehaviour.Murmur).apply {
					repulsionWeight = 0.01
					friction = 0.9
				}
			}
			2000 -> {
				(particleBehaviour as ParticleBehaviour.Murmur).apply {
					friction = 0.99
					repulsionWeight = 0.0
					simplexSpeed = 0.05
					simplexWeight = 0.1
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