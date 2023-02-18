import org.openrndr.KeyEvent
import org.openrndr.Program
import org.openrndr.color.ColorRGBa
import org.openrndr.extra.noise.gradient3D
import org.openrndr.extra.noise.simplex4D

private const val DRAW_DEBUG_INFO = false

class DevTools(private val program: Program,
			   private val nebula: Nebula) {

	fun listen(keyEvent: KeyEvent) {
		if (keyEvent.name == "s") {
			val prevSeed = nebula.mainGroup.simplexSeed
			reset()
			nebula.mainGroup.simplexSeed = prevSeed + 1
		}
		else if (keyEvent.name == "r") {
			reset()
		}
	}

	private fun reset() {
		nebula.particleGroups.clear()
        nebula.mainGroup.particles.clear()
        nebula.particleGroups.add(nebula.mainGroup)
        nebula.particleGroups.add(nebula.auxiliaryGroup)
		nebula.setup()
		nebula.frameNumber = 0
	}

	fun drawDebugInfo() {
		if (DRAW_DEBUG_INFO) program.drawer.apply {
			//debug text background
			fill = ColorRGBa.BLACK.opacify(0.5)
			stroke = null
			rectangle(0.0, 0.0, 210.0, 260.0)

			//debug text
			fill = ColorRGBa.WHITE
			text("frameNumber: ${nebula.frameNumber}", 20.0, 20.0)
			text("friction: ${nebula.mainGroup.friction}", 20.0, 60.0)
			text("simplexSeed: ${nebula.mainGroup.simplexSeed}", 20.0, 40.0)
			text("simplexScale: ${nebula.mainGroup.simplexScale}", 20.0, 80.0)
			text("simplexSpeed: ${nebula.mainGroup.simplexSpeed}", 20.0, 100.0)
			text("simplexWeight: ${nebula.mainGroup.simplexWeight}", 20.0, 120.0)
			text("nearScreenEdgeAccel: ${nebula.mainGroup.nearScreenEdgeAccel}", 20.0, 140.0)
			text("gravityWeight: ${nebula.mainGroup.gravityWeight}", 20.0, 160.0)
			text("repulsionWeight: ${nebula.mainGroup.repulsionWeight}", 20.0, 180.0)
			text("contourAttraction: ${nebula.mainGroup.contourAttraction}", 20.0, 200.0)
			text("contourAccel: ${nebula.mainGroup.contourAccel}", 20.0, 220.0)
			text("totalVelWeight: ${nebula.mainGroup.totalVelWeight}", 20.0, 240.0)

			//gradient indicator
			val gradient = gradient3D(
				noise = simplex4D,
				seed = nebula.mainGroup.simplexSeed,
				x = nebula.mainGroup.simplexScale * program.mouse.position.x,
				y = nebula.mainGroup.simplexScale * program.mouse.position.y,
				z = nebula.mainGroup.simplexSpeed * nebula.frameNumber.toDouble()
			).xy
			stroke = ColorRGBa.WHITE
			lineSegment(program.mouse.position,
						program.mouse.position + gradient * 10.0)
			lineSegment(program.mouse.position + gradient * 10.0,
						program.mouse.position + gradient * 10.0 + gradient.rotate(145.0) * 4.0)
			lineSegment(program.mouse.position + gradient * 10.0,
						program.mouse.position + gradient * 10.0 + gradient.rotate(215.0) * 4.0)

			nebula.auxiliaryGroup.contour?.let { contour ->
				//draw contour
				stroke = ColorRGBa.WHITE
                fill = null
				contour(contour)

				//draw projected point
				stroke = null
				fill = ColorRGBa.RED
				circle(contour.nearestPatch(program.mouse.position).position, 1.0)
			}
		}
	}
}