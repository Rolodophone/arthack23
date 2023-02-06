import org.openrndr.Fullscreen
import org.openrndr.application
import org.openrndr.color.ColorRGBa

fun main() = application {
    configure {
		width = 1920
		height = 1080
		fullscreen = Fullscreen.SET_DISPLAY_MODE
    }
    program {
		val nebula = Nebula(this)
		nebula.setup()

		val devTools = DevTools(this, nebula)
		keyboard.keyDown.listen(devTools::listen)

		extend {
			nebula.update()
			drawer.clear(ColorRGBa.BLACK)
			nebula.draw()
		}
    }
}