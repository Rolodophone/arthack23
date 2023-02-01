import org.openrndr.Program
import org.openrndr.extra.noise.Random

class Nebula(private val program: Program) {
	private val particles = mutableListOf<Particle>()

	fun setup() {
	}

	fun update() {
		when (program.frameCount) {
			in 0..399 -> {
				repeat(10) {
					val variance = Random.double()
					val newParticle = Particle(0.0, program.height/2.0 + 200*variance)
					val behaviour = ParticleBehaviour.Murmur(program)
					behaviour.vel.set(3.0, 3*variance)
					newParticle.behaviour = behaviour
					particles.add(newParticle)
				}
				repeat(10) {
					val variance = Random.double()
					val newParticle = Particle(program.width.toDouble(),
							program.height/2.0 + 200*variance)
					val behaviour = ParticleBehaviour.Murmur(program)
					behaviour.vel.set(-3.0, 3*variance)
					newParticle.behaviour = behaviour
					particles.add(newParticle)
				}
			}
			400 -> {
			}
		}

		particles.forEach { it.update() }
	}

	fun draw() {
		program.drawer.points {
			particles.forEach { it.draw(this) }
		}
	}
}