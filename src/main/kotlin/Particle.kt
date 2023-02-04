import org.openrndr.color.ColorRGBa
import org.openrndr.draw.PointBatchBuilder

class Particle(x: Double, y: Double) {
	val pos = MutableVector(x, y)
	val vel = MutableVector(0.0, 0.0)
	val colour = ColorRGBa.WHITE

	val draw: PointBatchBuilder.() -> Unit = {
		fill = colour
		point(pos.x, pos.y)
	}
}