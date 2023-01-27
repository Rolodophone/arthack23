import org.openrndr.Fullscreen
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.extra.noclear.NoClear
import org.openrndr.extra.olive.oliveProgram
import org.openrndr.math.Vector2
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random.Default.nextDouble
import kotlin.random.Random.Default.nextInt

/**
 *  This is a template for a live program.
 *
 *  It uses oliveProgram {} instead of program {}. All code inside the
 *  oliveProgram {} can be changed while the program is running.
 */

val turtles = mutableListOf<DoubleArray>() //x, y, direction, rate of change of direction
//val points = mutableListOf<Vector2>()

fun main() = application {
    configure {
		width = 1920
		height = 1080
		fullscreen = Fullscreen.SET_DISPLAY_MODE

    }
    oliveProgram {
		drawer.clear(ColorRGBa.WHITE)

		extend(NoClear())

        extend {
			//-----UPDATE-----
			
			for (turtle in turtles) {
				//add points to list
				//points.add(Vector2(turtle[0], turtle[1]))

				//move turtles
				turtle[0] += cos(turtle[2])
				turtle[1] -= sin(turtle[2])
				turtle[2] += turtle[3]
				
				//alter rate of change of direction
				turtle[3] += if (turtle[3] < -0.03) 0.01 * nextDouble()
						else if (turtle[3] > 0.03) -0.01 * nextDouble()
						else -0.005 + 0.01 * nextDouble()
			}

			//generate turtles at edge of screen
			turtles.add(
				when (nextInt(4)) {
					0 -> doubleArrayOf(0.0, nextInt(height).toDouble(), 0.0, 0.0)
					1 -> doubleArrayOf(width.toDouble(), nextInt(height).toDouble(), PI, 0.0)
					2 -> doubleArrayOf(nextInt(width).toDouble(), 0.0, -PI/2, 0.0)
					else -> doubleArrayOf(nextInt(width).toDouble(), height.toDouble(), PI/2, 0.0)
				}
			)


			//-----DRAW-----
			drawer.fill = ColorRGBa.GREEN
			drawer.points {
				for (turtle in turtles) {
					point(turtle[0], turtle[1])
				}
			}
        }
    }
}