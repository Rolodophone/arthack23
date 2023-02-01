import org.openrndr.color.ColorRGBa
import org.openrndr.draw.PointBatchBuilder

class Particle(x: Double, y: Double) {
	val pos = MutableVector(x, y)
	val colour = ColorRGBa.WHITE
	lateinit var behaviour: ParticleBehaviour

	fun update() {
		behaviour.update(this)
	}

	val draw: PointBatchBuilder.() -> Unit = {
		fill = colour
		point(pos.x, pos.y)
	}
}