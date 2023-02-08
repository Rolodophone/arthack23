import org.openrndr.math.Vector2
import org.openrndr.math.solveCubic
import org.openrndr.shape.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.sign

fun ShapeContour.nearestPatch(point: Vector2): ContourPoint {
	val n = segments.map { it.nearestPatch(point) }.minByOrNull { it.position.distanceTo(point) }
		?: error("no segments")
	val segmentIndex = segments.indexOf(n.segment)
	val t = (segmentIndex + n.segmentT) / segments.size
	return ContourPoint(this, t, n.segment, n.segmentT, n.position)
}

fun Segment.nearestPatch(point: Vector2): SegmentPoint {
	val t = when (type) {
		SegmentType.LINEAR -> {
			val dir = end - start
			val relativePoint = point - start
			((dir dot relativePoint) / dir.squaredLength).coerceIn(0.0, 1.0)
		}
		SegmentType.QUADRATIC -> {
			val qa = start - point
			val ab = control[0] - start
			val bc = end - control[0]
			val qc = end - point
			val ac = end - start
			val br = start + end - control[0] - control[0]

			var minDistance = sign(ab cross qa) * qa.length
			var param = -(qa dot ab) / (ab dot ab)

			val distance = sign(bc cross qc) * qc.length
			if (abs(distance) < abs(minDistance)) {
				minDistance = distance
				param =
					max(1.0, ((point - control[0]) dot bc) / (bc dot bc))
			}

			val a = br dot br
			val b = 3.0 * (ab dot br)
			val c = (2.0 * (ab dot ab)) + (qa dot br)
			val d = qa dot ab
			val ts = solveCubic(a, b, c, d)

			for (t in ts) {
				if (t > 0 && t < 1) {
					val endpoint = position(t)
					val distance2 = sign(ac cross (endpoint - point)) * (endpoint - point).length
					if (abs(distance2) < abs(minDistance)) {
						minDistance = distance2
						param = t
					}
				}
			}
			param.coerceIn(0.0, 1.0)
		}
		SegmentType.CUBIC -> {
			fun sign(n: Double): Double {
				val s = n.sign
				return if (s == 0.0) -1.0 else s
			}

			val qa = start - point
			val ab = control[0] - start
			val bc = control[1] - control[0]
			val cd = end - control[1]
			val qd = end - point
			val br = bc - ab
			val ax = (cd - bc) - br

			var minDistance = sign(ab cross qa) * qa.length
			var param = -(qa dot ab) / (ab dot ab)

			var distance = sign(cd cross qd) * qd.length
			if (abs(distance) < abs(minDistance)) {
				minDistance = distance
				param = max(1.0, (point - control[1] dot cd) / (cd dot cd))
			}
			val searchStarts = 5
			val searchSteps = 8

			for (i in 0 until searchStarts) {
				var t = i.toDouble() / (searchStarts - 1)
				var step = 0
				while (true) {
					val qpt = position(t) - point
					distance = sign(direction(t) cross qpt) * qpt.length
					if (abs(distance) < abs(minDistance)) {
						minDistance = distance
						param = t
					}
					if (step == searchSteps) {
						break
					}
					val d1 = (ax * (3 * t * t)) + br * (6 * t) + ab * 3.0
					val d2 = (ax * (6 * t)) + br * 6.0
					val dt = (qpt dot d1) / ((d1 dot d1) + (qpt dot d2))
					if (abs(dt) < 1e-14) {
						break
					}
					t -= dt
					if (t < 0 || t > 1) {
						break
					}
					step++
				}
			}
			param.coerceIn(0.0, 1.0)
		}
	}
	val closest = position(t)
	return SegmentPoint(this, t, closest)
}