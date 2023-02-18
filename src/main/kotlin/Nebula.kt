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
            in 0 until 500 -> {
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
            in 1500 until 5000 -> {
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
            in 5000 until 5500 -> {
                repeat(11) {
                    mainGroup.particles.removeFirst()
                }
                repeat(4) {
                    val pos = MutableVector(program.width / 2.0, program.height / 2.0) +
                            Vector2.uniformRing(innerRadius = 95.0, outerRadius = 105.0)
                    auxiliaryGroup.particles.add(Particle(pos))
                }
            }
            in 5500 until 6500 -> {
                auxiliaryGroup.contourAttraction += 0.00001
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