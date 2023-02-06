import org.openrndr.Program
import org.openrndr.extra.noise.Random
import kotlin.math.PI
import kotlin.math.cos

class Nebula(private val program: Program) {
	val particles = mutableListOf<Particle>()
	var particleBehaviour = ParticleBehaviour.Murmur(program)
	var frameNumber = 0

	fun setup() {
		particleBehaviour.apply {
			friction = 0.997
			simplexSeed = 0
			simplexScale = 0.005
			simplexSpeed = 0.005
			simplexWeight = 0.01
			nearScreenEdgeAccel = 0.0
			gravityWeight = 0.00002
			repulsionWeight = 0.0001
			totalVelWeight = 1.0
		}
	}

	fun update() {
		when (frameNumber) {
			in 0..399 -> {
				val cosInterp = 100*cos(0.005*PI*frameNumber - PI) + 100
				repeat(10) {
					val variance = Random.double()
					val newParticle = Particle(0.0, program.height/2.0 + cosInterp*variance)
					newParticle.vel.set(3.0, 3*variance)
					particles.add(newParticle)
				}
				repeat(10) {
					val variance = Random.double()
					val newParticle = Particle(program.width.toDouble(),
						program.height/2.0 + cosInterp*variance)
					newParticle.vel.set(-3.0, 3*variance)
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