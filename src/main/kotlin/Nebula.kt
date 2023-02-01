import org.openrndr.Program
import org.openrndr.extra.noise.Random

class Nebula(private val program: Program) {
	private val particles = mutableListOf<Particle>()

	fun setup() {
//		repeat(10000) {
//			val newParticle = Particle(
//				Random.double0(program.width.toDouble()),
//				Random.double0(program.height.toDouble()))
//			val behaviour = ParticleBehaviour.Murmur(program)
//			newParticle.behaviour = behaviour
//			particles.add(newParticle)
//		}
//		ParticleBehaviour.Murmur.avgPos.set(
//			particles.map { it.pos.x }.average(),
//			particles.map { it.pos.y }.average())
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
		}

		particles.forEach { it.update() }

		//update average
		ParticleBehaviour.Murmur.avgPos.set(
			particles.fold(MutableVector(0.0, 0.0)) { acc, particle -> acc + particle.pos } / particles.size.toDouble())
	}

	fun draw() {
		program.drawer.points {
			particles.forEach { it.draw(this) }
		}
	}
}