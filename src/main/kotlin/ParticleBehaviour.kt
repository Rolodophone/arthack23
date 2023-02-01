import org.openrndr.Program
import org.openrndr.extra.noise.gradient3D
import org.openrndr.extra.noise.simplex4D

sealed class ParticleBehaviour {
	abstract fun update(particle: Particle)

	class Velocity : ParticleBehaviour() {
		val vel = MutableVector(0.0, 0.0)

		override fun update(particle: Particle) {
			particle.pos.add(vel)
		}
	}

	class Murmur(private val program: Program) : ParticleBehaviour() {
		val vel = MutableVector(0.0, 0.0)

		override fun update(particle: Particle) {
			//acceleration from simplex noise
			val acc = MutableVector(gradient3D(
				simplex4D, 0,
				0.00001*particle.pos.x, 0.0005*particle.pos.y,
				0.00001*program.frameCount.toDouble()
			).xy)
			acc.scale(0.01)
			vel.add(acc)

			//acceleration for returning to centre
			vel.add(0.0001 * (program.width/2.0 - particle.pos.x),
					0.0001 * (program.height/2.0 - particle.pos.y))

			particle.pos.add(vel)
		}
	}
}
