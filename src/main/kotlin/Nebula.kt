import org.openrndr.Program
import org.openrndr.extra.noise.uniformRing
import org.openrndr.math.Vector2
import org.openrndr.shape.LineSegment
import org.openrndr.shape.contour

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
			contour = contour {
				moveTo(269.5555,547.774,)
				curveTo(265.744,526.984,235.56,518.129,231.47899999999998,497.33900000000006,)
				curveTo(220.8145,443.82400000000007,254.579,390.3090000000001,272.63550000000004,338.71899999999994,)
				curveTo(286.80350000000004,298.294,297.237,254.09600000000003,325.11100000000005,221.5635,)
				curveTo(354.60200000000003,187.06749999999997,395.64300000000003,159.5785,439.148,147.451,)
				curveTo(476.108,137.3255,521.5380000000001,130.6265,553.493,151.57049999999995,)
				curveTo(571.5880000000001,163.467,575.053,189.49300000000002,581.2130000000001,210.2445,)
				curveTo(586.218,226.838,581.2130000000001,246.011,588.528,261.68050000000005,)
				curveTo(595.843,277.119,628.183,283.279,623.5630000000001,299.83399999999995,)
				curveTo(621.638,307.149,607.008,300.21899999999994,602.003,305.994,)
				curveTo(588.143,321.779,605.468,351.80899999999997,592.763,368.749,)
				curveTo(577.363,388.769,531.1630000000001,376.0640000000001,523.8480000000001,399.9340000000001,)
				curveTo(503.443,463.84400000000005,567.353,540.0740000000001,625.488,573.569,)
				curveTo(651.283,588.1990000000001,685.548,579.729,714.423,573.569,)
				curveTo(792.578,557.0140000000001,920.783,453.44900000000007,933.103,477.70399999999995,)
				curveTo(939.263,489.6390000000001,904.228,497.33900000000006,909.618,509.659,)
				curveTo(919.628,533.529,985.078,493.874,986.6180000000002,520.054,)
				curveTo(988.928,557.784,911.158,531.989,876.508,547.774,)
				curveTo(825.6880000000001,570.874,781.028,611.6840000000001,738.678,644.024,)
				curveTo(693.633,678.2890000000001,646.6630000000001,675.594,599.693,670.204,)
				curveTo(575.053,667.124,531.548,639.404,531.548,639.404,)
				lineTo(456.47300000000007,827.669,)
				curveTo(456.47300000000007,827.669,578.903,829.2090000000001,634.728,850.384,)
				curveTo(684.393,868.864,750.613,889.2690000000001,766.398,939.704,)
				curveTo(782.183,988.9840000000002,715.578,1033.259,712.113,1084.849,)
				curveTo(754.8480000000001,1547.2340000000002,124.83399999999999,1546.4640000000002,553.8779999999999,1058.284,)
				curveTo(565.043,1045.579,594.303,1039.034,589.683,1023.249,)
				curveTo(571.5880000000001,963.5739999999998,468.023,993.989,406.42300000000006,986.674,)
				curveTo(344.515,978.9739999999999,280.4125,992.0640000000001,219.62100000000004,978.204,)
				curveTo(203.836,974.739,182.9305,973.5840000000001,174.88400000000001,959.3390000000002,)
				curveTo(149.243,914.6790000000001,183.93150000000003,856.544,194.442,806.109,)
				curveTo(212.65250000000003,718.329,285.764,635.9390000000001,269.5555,547.774,)
				close()
			}
		}
	}

	fun update() {
		when (frameNumber) {

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