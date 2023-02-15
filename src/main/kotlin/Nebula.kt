import org.openrndr.Program
import org.openrndr.extra.noise.uniformRing
import org.openrndr.math.Vector2
import org.openrndr.shape.LineSegment

class Nebula(private val program: Program) {
	val particles = mutableListOf<Particle>()
	var particleBehaviour = ParticleBehaviour.Murmur(program)
	var frameNumber = 0

	fun setup() {
		particleBehaviour.apply {
			friction = 1.0
			simplexSeed = 0
			simplexScale = 0.005
			simplexSpeed = 0.005
			simplexWeight = 0.0
			nearScreenEdgeAccel = 0.0
			gravityWeight = 0.0
			repulsionWeight = 0.0
			contourAttraction = 0.002
			contourAccel = 0.0
			totalVelWeight = 1.0
			contour = LineSegment(program.width/2.0, program.height/2.0 - 110.0,
								  program.width/2.0, program.height/2.0 + 110.0).contour
		}
	}

	fun update() {
		when (frameNumber) {
			in 0..100 -> {
				repeat(10) {
					val pos = MutableVector(program.width / 2.0, program.height / 2.0) +
							Vector2.uniformRing(innerRadius = 95.0, outerRadius = 105.0)
					particles.add(Particle(pos))
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