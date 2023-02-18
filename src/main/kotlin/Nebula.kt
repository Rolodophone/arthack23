import org.openrndr.Program
import org.openrndr.draw.DrawQuality
import org.openrndr.draw.loadImage
import org.openrndr.extra.noise.Random
import org.openrndr.extra.noise.uniformRing
import org.openrndr.math.Matrix55
import org.openrndr.math.Vector2
import org.openrndr.shape.Circle
import org.openrndr.shape.LineSegment
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
            repulsionWeight = 0.00002,
        )
	}

	fun update() {
		when (frameNumber) {
            in 0..499 -> {
                repeat(6) {
                    val variance = Random.double()
                    val newParticle = Particle(0.0, program.height/2.0 + 100*variance)
                    newParticle.vel.set(2.0, 1.3*variance)
                    mainGroup.particles.add(newParticle)
                }
                repeat(6) {
                    val variance = Random.double()
                    val newParticle = Particle(program.width.toDouble(),
                        program.height/2.0 + 100*variance)
                    newParticle.vel.set(-2.0, 1.3*variance)
                    mainGroup.particles.add(newParticle)
                }
            }
            1500 -> {
                mainGroup.resetParameters(
                    friction = 0.997,
                    contourAttraction = 0.03,
                    contourAttractionReach = 1000.0,
                    contourAccel = 0.01,
                    contour = Circle(program.width / 2.0, program.height / 2.0, 200.0).contour,
                )
            }
            in 1501 until 4999 -> {
                mainGroup.contourAccel += 0.00002
            }
            5000 -> {
                mainGroup.friction = 0.99
                auxiliaryGroup.resetParameters(
                    contourAttraction = 0.002,
                    contourAttractionReach = 1000.0,
                    contour = LineSegment(program.width/2.0, program.height/2.0 - 110.0,
                    program.width/2.0, program.height/2.0 + 110.0).contour
                )
            }
            in 5001..5499 -> {
                repeat(11) {
                    mainGroup.particles.removeFirstOrNull()
                }
                repeat(4) {
                    val pos = MutableVector(program.width / 2.0, program.height / 2.0) +
                            Vector2.uniformRing(innerRadius = 95.0, outerRadius = 105.0)
                    auxiliaryGroup.particles.add(Particle(pos))
                }
            }
            6000 -> {
                mainGroup.resetParameters(
                    friction = 0.96,
                    simplexSpeed = 0.05,
                    simplexWeight = 0.1,
                )
            }
            in 6001..6499 -> {
                auxiliaryGroup.contourAttraction -= 0.0001
                repeat(4) {
                    auxiliaryGroup.particles.removeFirstOrNull()
                }
                repeat(10) {
                    mainGroup.particles.add(Particle(Random.double0(program.width.toDouble()), 0.0).apply {
                        vel.set(0.0, 2.0)
                    })
                }
                repeat(10) {
                    mainGroup.particles.add(Particle(Random.double0(program.width.toDouble()),
                            program.height.toDouble()).apply {
                        vel.set(0.0, -2.0)
                    })
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