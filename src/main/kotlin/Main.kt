import org.openrndr.Fullscreen
import org.openrndr.application
import org.openrndr.color.ColorRGBa

/**
 *  This is a template for a live program.
 *
 *  It uses oliveProgram {} instead of program {}. All code inside the
 *  oliveProgram {} can be changed while the program is running.
 */

//val points = mutableListOf<Vector2>()

val BG_COLOUR = ColorRGBa.fromHex("C6C5B9")

fun main() = application {
    configure {
		width = 1920
		height = 1080
		fullscreen = Fullscreen.SET_DISPLAY_MODE

    }
    program {
		val nebula = Nebula(this)
		nebula.setup()

		extend {
			nebula.update()
			drawer.clear(ColorRGBa.BLACK)
			nebula.draw()
		}
    }
}