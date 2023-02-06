import org.openrndr.KeyEvent
import org.openrndr.Program

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
}