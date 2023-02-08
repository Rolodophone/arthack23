import org.openrndr.Program
import org.openrndr.extra.noise.Random

class Nebula(private val program: Program) {
	val particles = mutableListOf<Particle>()
	var particleBehaviour = ParticleBehaviour.Murmur(program)
	var frameNumber = 0

	fun setup() {
		particleBehaviour.apply {
			friction = 0.99
			simplexSeed = 0
			simplexScale = 0.005
			simplexSpeed = 0.005
			simplexWeight = 0.01
			nearScreenEdgeAccel = 0.0
			gravityWeight = 0.00002
			repulsionWeight = 0.0001
			contourAttraction = 0.01
			contourAccel = 0.2
			totalVelWeight = 1.0
		}
	}

	fun update() {
		when (frameNumber) {
			in 0..399 -> {
				repeat(10) {
					val variance = Random.double()
					val newParticle = Particle(0.0,
						50.0 + 50.0*variance)
					newParticle.vel.set(3.0, 1*variance)
					particles.add(newParticle)
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