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
		mainGroup.apply {
			friction = 0.99
			simplexSeed = 0
			simplexScale = 0.005
			simplexSpeed = 0.005
			simplexWeight = 0.002
			nearScreenEdgeAccel = 0.01
			gravityWeight = 0.0
			repulsionWeight = 0.00001
			contourAttraction = 0.0
            contourAttractionReach = 100.0
            contourAttractionNormalised = true
			contourAccel = 0.0
			totalVelWeight = 1.0
            contour = null

            repeat(5000) {
                particles.add(Particle(Random.double0(program.width.toDouble()),
                                       Random.double0(program.height.toDouble())))
            }
		}

        auxiliaryGroup.apply {
            friction = 0.99
            simplexSeed = 1
            simplexScale = 0.005
            simplexSpeed = 0.005
            simplexWeight = 0.0001
            nearScreenEdgeAccel = 0.0
            gravityWeight = 0.0
            repulsionWeight = 0.0
            contourAttraction = 0.0
            contourAttractionReach = 100.0
            contourAttractionNormalised = true
            contourAccel = 0.0
            totalVelWeight = 1.0
            contour = contour {
                moveTo(188.89800000000002,874.854,)
                curveTo(188.59000000000003,834.044,191.40050000000002,800.9340000000001,196.7905,773.984,)
                curveTo(213.9615,688.5140000000001,285.148,610.359,269.5555,525.274,)
                curveTo(265.744,504.48400000000004,235.56,495.629,231.47899999999998,474.83900000000006,)
                curveTo(220.8145,421.32400000000007,254.579,367.8090000000001,272.63550000000004,316.21899999999994,)
                curveTo(286.80350000000004,275.794,297.237,231.59600000000003,325.11100000000005,199.0635,)
                curveTo(354.60200000000003,164.56749999999997,395.64300000000003,137.0785,439.148,124.951,)
                curveTo(476.108,114.8255,521.5380000000001,108.1265,553.493,129.07049999999995,)
                curveTo(571.5880000000001,140.967,575.053,166.99300000000002,581.2130000000001,187.7445,)
                curveTo(586.218,204.338,581.2130000000001,223.511,588.528,239.18050000000002,)
                curveTo(595.843,254.619,628.183,260.779,623.5630000000001,277.33399999999995,)
                curveTo(621.638,284.649,607.008,277.71899999999994,602.003,283.494,)
                curveTo(588.143,299.279,605.468,329.30899999999997,592.763,346.249,)
                curveTo(577.363,366.269,531.1630000000001,353.5640000000001,523.8480000000001,377.4340000000001,)
                curveTo(503.443,441.34400000000005,567.353,517.5740000000001,625.488,551.069,)
                curveTo(651.283,565.6990000000001,685.548,557.229,714.423,551.069,)
                curveTo(792.578,534.5140000000001,920.783,430.94900000000007,933.103,455.20399999999995,)
                curveTo(939.263,467.1390000000001,904.228,474.83900000000006,909.618,487.159,)
                curveTo(919.628,511.029,985.078,471.374,986.6180000000002,497.554,)
                curveTo(988.928,535.284,911.158,509.48900000000003,876.508,525.274,)
                curveTo(825.6880000000001,548.374,781.028,589.1840000000001,738.678,621.524,)
                curveTo(693.633,655.7890000000001,646.6630000000001,653.094,599.693,647.704,)
                curveTo(575.053,644.624,531.548,616.904,531.548,616.904,)
                lineTo(456.47300000000007,805.169,)
                curveTo(456.47300000000007,805.169,602.003,787.844,739.8330000000001,876.779,)
            }
        }
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
        program.drawer.drawStyle.colorMatrix = Matrix55(0.054, 0.000, 0.000, 0.000, 0.013,
                                                        0.000, 0.054, 0.000, 0.000, 0.013,
                                                        0.000, 0.000, 0.054, 0.000, 0.013,
                                                        0.000, 0.000, 0.000, 1.000, 0.000)
        program.drawer.image(image)
        program.drawer.drawStyle.colorMatrix = Matrix55.IDENTITY
		particleGroups.forEach { it.draw(imageShadow) }
	}
}