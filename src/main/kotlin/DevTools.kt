import org.openrndr.KeyEvent
import org.openrndr.Program
import org.openrndr.color.ColorRGBa

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
		program.drawer.apply {
			//background
			fill = ColorRGBa.BLACK.opacify(0.5)
			stroke = null
			rectangle(0.0, 0.0, 210.0, 190.0)

			fill = ColorRGBa.WHITE
			text("frameNumber: ${nebula.frameNumber}", 20.0, 20.0)
			text("simplexSeed: ${nebula.particleBehaviour.simplexSeed}", 20.0, 40.0)
			text("friction: ${nebula.particleBehaviour.friction}", 20.0, 60.0)
			text("simplexScale: ${nebula.particleBehaviour.simplexScale}", 20.0, 80.0)
			text("simplexSpeed: ${nebula.particleBehaviour.simplexSpeed}", 20.0, 100.0)
			text("simplexWeight: ${nebula.particleBehaviour.simplexWeight}", 20.0, 120.0)
			text("repulsionWeight: ${nebula.particleBehaviour.repulsionWeight}", 20.0, 140.0)
			text("gravityWeight: ${nebula.particleBehaviour.gravityWeight}", 20.0, 160.0)
		}
	}
}