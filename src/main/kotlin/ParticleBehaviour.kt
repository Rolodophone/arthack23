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
		companion object {
			val avgPos = MutableVector()
		}

		val vel = MutableVector(0.0, 0.0)

		override fun update(particle: Particle) {
			//friction
			vel.scale(0.997)

			//acceleration from simplex noise
			vel.add(MutableVector(gradient3D(
				noise = simplex4D,
				seed = 0,
				x = 0.0005*particle.pos.x,
				y = 0.0005*particle.pos.y,
				z = 0.005*program.frameCount.toDouble()
			).xy) * 0.01)

			//bounce back to the screen
			when {
				particle.pos.x > program.width + 100
						|| particle.pos.x < -100 -> vel.x = -vel.x
				particle.pos.y > program.height + 100
						|| particle.pos.y < -100 -> vel.y = -vel.y
			}

//			val velScale =
//				if (program.frameCount >= frameVelLastChanged + 100) 1.0
//				else 0.01 * (program.frameCount - frameVelLastChanged)
//			particle.pos.add(velScale*vel.x, velScale*vel.y)
//			particle.pos.add((1-velScale)*prevVel.x, (1-velScale)*prevVel.y)

			//gravity
			vel.add((avgPos - particle.pos) * 0.00002)

			particle.pos.add(vel)

//			//update average
//			avgPos.add(vel * 0.0001)
		}
	}
}
