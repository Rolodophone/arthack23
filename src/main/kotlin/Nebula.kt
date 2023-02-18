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
            friction = 0.99,
            randomAccel = 1.0,
            colorBufferShadow = imageShadow
        )
        mainGroup.particles = MutableList(50_000) {
            Particle(Random.double0(program.width.toDouble()),
                     Random.double0(program.height.toDouble()))
        }

        auxiliaryGroup.resetParameters(
            friction = 0.9,
            contourAttraction = 0.2,
            contourAccel = 0.1,
            randomAccel = 1.0,
            contour = contour {
                moveTo(188.9, 874.85,)
                curveTo(188.59, 834.04, 191.4, 800.93, 196.79, 773.98,)
                curveTo(213.96, 688.51, 285.15, 610.36, 269.56, 525.27,)
                curveTo(265.74, 504.48, 235.56, 495.63, 231.48, 474.84,)
                curveTo(220.81, 421.32, 254.58, 367.81, 272.64, 316.22,)
                curveTo(286.8, 275.79, 297.24, 231.6, 325.11, 199.06,)
                curveTo(354.6, 164.57, 395.64, 137.08, 439.15, 124.95,)
                curveTo(476.11, 114.83, 521.54, 108.13, 553.49, 129.07,)
                curveTo(571.59, 140.97, 575.05, 166.99, 581.21, 187.74,)
                curveTo(586.22, 204.34, 581.21, 223.51, 588.53, 239.18,)
                curveTo(595.84, 254.62, 628.18, 260.78, 623.56, 277.33,)
                curveTo(621.64, 284.65, 607.01, 277.72, 602.0, 283.49,)
                curveTo(588.14, 299.28, 605.47, 329.31, 592.76, 346.25,)
                curveTo(577.36, 366.27, 531.16, 353.56, 523.85, 377.43,)
                curveTo(503.44, 441.34, 567.35, 517.57, 625.49, 551.07,)
                curveTo(651.28, 565.7, 685.55, 557.23, 714.42, 551.07,)
                curveTo(792.58, 534.51, 920.78, 430.95, 933.1, 455.2,)
                curveTo(939.26, 467.14, 904.23, 474.84, 909.62, 487.16,)
                curveTo(919.63, 511.03, 985.08, 471.37, 986.62, 497.55,)
                curveTo(988.93, 535.28, 911.16, 509.49, 876.51, 525.27,)
                curveTo(825.69, 548.37, 781.03, 589.18, 738.68, 621.52,)
                curveTo(693.63, 655.79, 646.66, 653.09, 599.69, 647.7,)
                curveTo(575.05, 644.62, 531.55, 616.9, 531.55, 616.9,)
                lineTo(456.47, 805.17,)
                curveTo(456.47, 805.17, 602.0, 787.84, 739.83, 876.78,)
            }
        )
	}

	fun update() {
		when (frameNumber) {
            in 0..1000 -> {
                repeat(10) {
                    auxiliaryGroup.particles.add(Particle(
                        auxiliaryGroup.contour!!.pointAtLength(
                            Random.double0(auxiliaryGroup.contour!!.length),
                            distanceTolerance = 10.0
                        )
                    ))
                }
            }
		}

		particleGroups.forEach { it.update(frameNumber) }

		frameNumber++
	}

	fun draw() {
        program.drawer.drawStyle.quality = DrawQuality.PERFORMANCE
        program.drawer.drawStyle.colorMatrix = Matrix55(0.248, 0.000, 0.000, 0.000, -0.004,
                                                        0.000, 0.248, 0.000, 0.000, -0.004,
                                                        0.000, 0.000, 0.248, 0.000, -0.004,
                                                        0.000, 0.000, 0.000, 1.000, 0.000)
        program.drawer.image(image)
        program.drawer.drawStyle.colorMatrix = Matrix55.IDENTITY
		particleGroups.forEach { it.draw() }
	}
}