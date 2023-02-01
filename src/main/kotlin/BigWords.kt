import org.openrndr.Program
import org.openrndr.draw.FontMap
import org.openrndr.draw.loadFont

class BigWords(private val program: Program) {
	private val drawer = program.drawer
	private lateinit var font: FontMap

	fun setup() {
		font = loadFont("data/fonts/SwingHappy.otf", 500.0)
	}

	fun draw() {
		drawer.fontMap = font
		drawer.fill = BG_COLOUR
		drawer.text("be kind", program.width/2 - 500.0, program.height/2 + 100.0)
	}
}