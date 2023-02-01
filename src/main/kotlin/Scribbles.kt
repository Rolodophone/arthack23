import org.openrndr.Program
import org.openrndr.color.ColorRGBa
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random.Default.nextDouble

val TURTLE_COLOUR = ColorRGBa.fromHex("17301C")

class Scribbles(private val program: Program) {
	private val drawer = program.drawer

	private val turtles = mutableListOf<DoubleArray>() //x, y, direction, rate of change of direction

	fun setup() {
		//generate turtles
		repeat(100) {
			turtles.add(
				doubleArrayOf(nextDouble() * program.width, nextDouble() * program.height,
						nextDouble() * 2 * PI, 0.0)
			)
		}
	}

	fun draw() {
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


		//draw turtles
		drawer.fill = TURTLE_COLOUR
		drawer.strokeWeight = 0.0
		drawer.circles {
			for (turtle in turtles) {
				circle(turtle[0], turtle[1], 2.0)
			}
		}
	}
}