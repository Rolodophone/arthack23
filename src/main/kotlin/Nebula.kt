import org.openrndr.Program
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.ColorBuffer
import org.openrndr.draw.DrawQuality
import org.openrndr.extra.noise.Random
import org.openrndr.extra.noise.uniformRing
import org.openrndr.math.Matrix55
import org.openrndr.math.Vector2
import org.openrndr.shape.Circle
import org.openrndr.shape.LineSegment
import org.openrndr.shape.contour

const val PHASE1 = 0
const val PHASE2 = 1500
const val PHASE3 = 2800
const val PHASE4 = 3800
const val PHASE5 = 4500
const val PHASE6 = 5700

class Nebula(private val program: Program, private val assets: Assets) {
	var mainGroup = ParticleGroup(program)
    var auxiliaryGroup = ParticleGroup(program)
    var particleGroups = mutableListOf(mainGroup, auxiliaryGroup)
	var frameNumber = 0
    var bgImage: ColorBuffer? = null
    var bgImageMatrix = Matrix55.IDENTITY

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
            in PHASE1..PHASE1+499 -> { //phase 1
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
            PHASE2 -> { //PHASE 2
                mainGroup.resetParameters(
                    friction = 0.997,
                    contourAttraction = 0.03,
                    contourAttractionReach = 1000.0,
                    contourAccel = 0.01,
                    contour = Circle(program.width / 2.0, program.height / 2.0, 200.0).contour,
                )
            }
            in PHASE2+1..PHASE2+1000 -> {
                mainGroup.contourAccel += 0.00005
            }
            PHASE3 -> { //phase 3
                mainGroup.friction = 0.99
                auxiliaryGroup.resetParameters(
                    contourAttraction = 0.002,
                    contourAttractionReach = 1000.0,
                    contour = LineSegment(program.width/2.0, program.height/2.0 - 110.0,
                    program.width/2.0, program.height/2.0 + 110.0).contour
                )
            }
            in PHASE3+1..PHASE3+499 -> {
                repeat(11) {
                    mainGroup.particles.removeFirstOrNull()
                }
                repeat(4) {
                    val pos = MutableVector(program.width / 2.0, program.height / 2.0) +
                            Vector2.uniformRing(innerRadius = 95.0, outerRadius = 105.0)
                    auxiliaryGroup.particles.add(Particle(pos))
                }
            }
            PHASE4 -> { //phase 4
                mainGroup.resetParameters(
                    friction = 0.96,
                    simplexSpeed = 0.05,
                    simplexWeight = 0.1,
                )
            }
            in PHASE4+1..PHASE4+499 -> {
                auxiliaryGroup.contourAttraction -= 0.0001
                repeat(10) {
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
            PHASE5 -> { //phase 5
                mainGroup.colorBufferShadow = assets.conversationImageShadow
                mainGroup.colorOffset = ColorRGBa(1.0, 1.0, 1.0, 0.0)
                mainGroup.particleWidth = 1
                mainGroup.varyParticleWidth = false
            }
            in PHASE5+1..PHASE5+499 -> {
                mainGroup.colorOffset -= ColorRGBa(0.001, 0.001, 0.001, 0.0)
                mainGroup.friction += 0.00002
                mainGroup.simplexSpeed -= 0.0001
                mainGroup.simplexWeight -= 0.0002
                mainGroup.randomAccel += 0.001
                repeat(50) {
                    mainGroup.particles.add(Particle(Random.double0(program.width.toDouble()),
                                                     Random.double0(program.height.toDouble())))
                }
            }
            PHASE5+500 -> {
                bgImage = assets.conversationImage
                bgImageMatrix = Matrix55(0.0, 0.0, 0.0, 0.0, 0.0,
                                         0.0, 0.0, 0.0, 0.0, 0.0,
                                         0.0, 0.0, 0.0, 0.0, 0.0,
                                         0.0, 0.0, 0.0, 1.0, 0.0,
                                         0.0, 0.0, 0.0, 0.0, 1.0)
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
                    },
                    particleWidth = 2,
                    varyParticleWidth = true,
                )
            }
            in PHASE5+501..PHASE5+999 -> {
                bgImageMatrix += Matrix55(0.0002, 0.0, 0.0, 0.0, 0.0,
                                          0.0, 0.0002, 0.0, 0.0, 0.0,
                                          0.0, 0.0, 0.0002, 0.0, 0.0,
                                          0.0, 0.0, 0.0, 0.0, 0.0,
                                          0.0, 0.0, 0.0, 0.0, 0.0)
                repeat(5) {
                    auxiliaryGroup.particles.add(Particle(
                            auxiliaryGroup.contour!!.pointAtLength(
                                    Random.double0(auxiliaryGroup.contour!!.length),
                                    distanceTolerance = 10.0
                            )
                    ))
                }
            }
            PHASE6 -> { //phase 6
                auxiliaryGroup.resetParameters()
            }
            in PHASE6+1..PHASE6+199 -> {
                repeat(20) { auxiliaryGroup.particles.removeFirstOrNull() }
                mainGroup.colorOffset += ColorRGBa(0.0025, 0.0025, 0.0025, 0.0)
                bgImageMatrix += Matrix55(-0.0005, 0.0, 0.0, 0.0, 0.0,
                                          0.0, -0.0005, 0.0, 0.0, 0.0,
                                          0.0, 0.0, -0.0005, 0.0, 0.0,
                                          0.0, 0.0, 0.0, 0.0, 0.0,
                                          0.0, 0.0, 0.0, 0.0, 0.0)
            }
            PHASE6+200 -> {
                bgImage = assets.awakeningImage
                bgImageMatrix = Matrix55(0.0, 0.0, 0.0, 0.0, 0.0,
                                         0.0, 0.0, 0.0, 0.0, 0.0,
                                         0.0, 0.0, 0.0, 0.0, 0.0,
                                         0.0, 0.0, 0.0, 1.0, 0.0,
                                         0.0, 0.0, 0.0, 0.0, 1.0)
                mainGroup.colorBufferShadow = assets.awakeningImageShadow
                auxiliaryGroup.resetParameters(
                    friction = 0.9,
                    contourAttraction = 0.2,
                    contourAccel = 0.5,
                    randomAccel = 2.0,
                    contour = Circle(615.0, 540.0, 100.0).contour,
                    particleWidth = 3,
                    varyParticleWidth = true,
                )
            }
            in PHASE6+201..PHASE6+399 -> {
                mainGroup.colorOffset -= ColorRGBa(0.0025, 0.0025, 0.0025, 0.0)
                bgImageMatrix += Matrix55(0.001, 0.0, 0.0, 0.0, 0.0,
                                          0.0, 0.001, 0.0, 0.0, 0.0,
                                          0.0, 0.0, 0.001, 0.0, 0.0,
                                          0.0, 0.0, 0.0, 0.0, 0.0,
                                          0.0, 0.0, 0.0, 0.0, 0.0)
                repeat(5) {
                    auxiliaryGroup.particles.add(Particle(
                        auxiliaryGroup.contour!!.pointAtLength(Random.double0(auxiliaryGroup.contour!!.length),
                                distanceTolerance = 10.0)
                    ))
                }
            }

		}

		particleGroups.forEach { it.update(frameNumber) }

		frameNumber++
	}

	fun draw() {
        program.drawer.drawStyle.quality = DrawQuality.PERFORMANCE
        if (bgImage != null) {
            program.drawer.drawStyle.colorMatrix = bgImageMatrix
            program.drawer.image(bgImage!!)
        }
		particleGroups.forEach { it.draw() }
	}
}