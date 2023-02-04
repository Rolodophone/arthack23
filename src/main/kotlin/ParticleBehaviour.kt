import org.openrndr.Program
import org.openrndr.extra.noise.gradient3D
import org.openrndr.extra.noise.simplex4D

sealed class ParticleBehaviour {
	open fun update(particles: List<Particle>) {}
	open fun updateParticle(particle: Particle) {}

	class Murmur(private val program: Program) : ParticleBehaviour() {
		val avgPos = MutableVector()

		override fun update(particles: List<Particle>) {
			//update average
			avgPos.set(
				particles.fold(MutableVector(0.0, 0.0)) { acc, particle -> acc + particle.pos } / particles.size.toDouble())
		}

		override fun updateParticle(particle: Particle) {
			//friction
			particle.vel.scale(0.96)

			//acceleration from simplex noise
			particle.vel.add(MutableVector(gradient3D(
				noise = simplex4D,
				seed = 0,
				x = 0.005*particle.pos.x,
				y = 0.005*particle.pos.y,
				z = 0.05*program.frameCount.toDouble()
			).xy) * 0.1)

			//bounce back to the screen
			when {
				particle.pos.x > program.width + 100
						|| particle.pos.x < -100 -> particle.vel.x = -particle.vel.x
				particle.pos.y > program.height + 100
						|| particle.pos.y < -100 -> particle.vel.y = -particle.vel.y
			}

//			val particle.velScale =
//				if (program.frameCount >= frameVelLastChanged + 100) 1.0
//				else 0.01 * (program.frameCount - frameVelLastChanged)
//			particle.pos.add(particle.velScale*particle.vel.x, particle.velScale*particle.vel.y)
//			particle.pos.add((1-particle.velScale)*prevVel.x, (1-particle.velScale)*prevVel.y)

			//gravity
			particle.vel.add((avgPos - particle.pos) * 0.0000)

			particle.pos.add(particle.vel)

//			//update average
//			avgPos.add(vel * 0.0001)
		}
	}
}
