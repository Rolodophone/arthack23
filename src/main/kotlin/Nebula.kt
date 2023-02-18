import org.openrndr.Program
import org.openrndr.draw.DrawQuality
import org.openrndr.draw.loadImage
import org.openrndr.extra.noise.Random
import org.openrndr.math.Matrix55
import org.openrndr.shape.contour

class Nebula(private val program: Program) {
	var mainGroup = ParticleGroup(program)
    var auxiliaryGroup = ParticleGroup(program)
    var particleGroups = mutableListOf(mainGroup, auxiliaryGroup)
	var frameNumber = 0
    var image = loadImage("data/images/conversation.png")
    var imageShadow = image.shadow.apply { download() }

	fun setup() {
        mainGroup.resetParameters(
            friction = 0.997,
            simplexWeight = 0.01,
            gravityWeight = 0.00002,
        )
	}

	fun update() {
		when (frameNumber) {
            in 0..399 -> {
                repeat(10) {
                    val variance = Random.double()
                    val newParticle = Particle(0.0, program.height/2.0 + 100*variance)
                    newParticle.vel.set(3.0, 3*variance)
                    mainGroup.particles.add(newParticle)
                }
                repeat(10) {
                    val variance = Random.double()
                    val newParticle = Particle(program.width.toDouble(),
                        program.height/2.0 + 100*variance)
                    newParticle.vel.set(-3.0, 3*variance)
                    mainGroup.particles.add(newParticle)
                }
            }
		}

		particleGroups.forEach { it.update(frameNumber) }

		frameNumber++
	}

	fun draw() {
        program.drawer.drawStyle.quality = DrawQuality.PERFORMANCE
		particleGroups.forEach { it.draw() }
	}
}