import org.openrndr.color.ColorRGBa
import org.openrndr.draw.ColorBufferShadow
import org.openrndr.draw.PointBatchBuilder
import org.openrndr.math.Vector2

class Particle(x: Double, y: Double) {
	constructor(vector2: Vector2) : this(vector2.x, vector2.y)
	constructor(mutVec: MutableVector) : this(mutVec.x, mutVec.y)

	val pos = MutableVector(x, y)
	val vel = MutableVector(0.0, 0.0)
}