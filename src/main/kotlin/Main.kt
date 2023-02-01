import org.openrndr.Fullscreen
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.extra.compositor.compose
import org.openrndr.extra.compositor.draw
import org.openrndr.extra.compositor.layer
import org.openrndr.extra.noclear.NoClear
import org.openrndr.extra.olive.oliveProgram

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
    oliveProgram {
		extend(NoClear().apply {
			backdrop = {
				drawer.clear(BG_COLOUR)
			}
		})

		val scribbles = Scribbles(this)
		val bigWords = BigWords(this)

		val composite = compose {
			layer {
				scribbles.setup()
				draw {
					scribbles.draw()
				}
			}

			layer {
				bigWords.setup()
				draw {
					bigWords.draw()
				}
			}
        }

		extend {
			composite.draw(drawer)
		}
    }
}