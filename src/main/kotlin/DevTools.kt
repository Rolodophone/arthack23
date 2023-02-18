import org.openrndr.KeyEvent
import org.openrndr.Program
import org.openrndr.color.ColorRGBa
import org.openrndr.extra.noise.gradient3D
import org.openrndr.extra.noise.simplex4D

private const val ENABLED = true

class DevTools(private val program: Program,
			   private val nebula: Nebula) {

	fun listen(keyEvent: KeyEvent) {
		if (keyEvent.name == "s") {
			val prevSeed = nebula.particleBehaviour.simplexSeed
			reset()
			nebula.particleBehaviour.simplexSeed = prevSeed + 1
		}
		else if (keyEvent.name == "r") {
			reset()
		}
	}

	private fun reset() {
		nebula.particles.clear()
		nebula.setup()
		nebula.frameNumber = 0
	}

	fun drawDebugInfo() {
		if (ENABLED) program.drawer.apply {
			//debug text background
			fill = ColorRGBa.BLACK.opacify(0.5)
			stroke = null
			rectangle(0.0, 0.0, 210.0, 260.0)

			//debug text
			fill = ColorRGBa.WHITE
			text("frameNumber: ${nebula.frameNumber}", 20.0, 20.0)
			text("friction: ${nebula.particleBehaviour.friction}", 20.0, 60.0)
			text("simplexSeed: ${nebula.particleBehaviour.simplexSeed}", 20.0, 40.0)
			text("simplexScale: ${nebula.particleBehaviour.simplexScale}", 20.0, 80.0)
			text("simplexSpeed: ${nebula.particleBehaviour.simplexSpeed}", 20.0, 100.0)
			text("simplexWeight: ${nebula.particleBehaviour.simplexWeight}", 20.0, 120.0)
			text("nearScreenEdgeAccel: ${nebula.particleBehaviour.nearScreenEdgeAccel}", 20.0, 140.0)
			text("gravityWeight: ${nebula.particleBehaviour.gravityWeight}", 20.0, 160.0)
			text("repulsionWeight: ${nebula.particleBehaviour.repulsionWeight}", 20.0, 180.0)
			text("contourAttraction: ${nebula.particleBehaviour.contourAttraction}", 20.0, 200.0)
			text("contourAccel: ${nebula.particleBehaviour.contourAccel}", 20.0, 220.0)
			text("totalVelWeight: ${nebula.particleBehaviour.totalVelWeight}", 20.0, 240.0)

			//gradient indicator
			val gradient = gradient3D(
				noise = simplex4D,
				seed = nebula.particleBehaviour.simplexSeed,
				x = nebula.particleBehaviour.simplexScale * program.mouse.position.x,
				y = nebula.particleBehaviour.simplexScale * program.mouse.position.y,
				z = nebula.particleBehaviour.simplexSpeed * nebula.frameNumber.toDouble()
			).xy
			stroke = ColorRGBa.WHITE
			lineSegment(program.mouse.position,
						program.mouse.position + gradient * 10.0)
			lineSegment(program.mouse.position + gradient * 10.0,
						program.mouse.position + gradient * 10.0 + gradient.rotate(145.0) * 4.0)
			lineSegment(program.mouse.position + gradient * 10.0,
						program.mouse.position + gradient * 10.0 + gradient.rotate(215.0) * 4.0)

			nebula.particleBehaviour.contour?.let { contour ->
				//draw contour
				stroke = ColorRGBa.WHITE
                fill = null
				contour(contour)

				//draw projected point
				stroke = null
				fill = ColorRGBa.RED
				circle(contour.nearestPatch(program.mouse.position).position, 5.0)
			}
		}
	}
}