import org.openrndr.Program
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.ColorBufferShadow
import org.openrndr.draw.PointBatchBuilder
import org.openrndr.extra.noise.Random
import org.openrndr.extra.noise.gradient3D
import org.openrndr.extra.noise.simplex4D
import org.openrndr.extra.noise.uniformRing
import org.openrndr.math.Vector2
import org.openrndr.shape.Circle
import org.openrndr.shape.ShapeContour
import kotlin.math.absoluteValue
import kotlin.math.pow

class ParticleGroup(private val program: Program) {
    companion object {
        const val GRID_SQUARE_SIZE = 30
        const val GRID_OFFSCREEN_MARGIN = 2
        const val GRID_ONSCREEN_WIDTH = 1920 / GRID_SQUARE_SIZE
        const val GRID_ONSCREEN_HEIGHT = 1080 / GRID_SQUARE_SIZE
        const val GRID_TOTAL_WIDTH = GRID_ONSCREEN_WIDTH + GRID_OFFSCREEN_MARGIN * 2
        const val GRID_TOTAL_HEIGHT = GRID_ONSCREEN_HEIGHT + GRID_OFFSCREEN_MARGIN * 2
    }

    private val avgPos = MutableVector()
    private val particleGrid = Array(GRID_TOTAL_HEIGHT) { Array(GRID_TOTAL_WIDTH) { mutableListOf<Particle>() } }

    var particles = mutableListOf<Particle>()

    var friction = 1.0
    var simplexSeed = 0
    var simplexScale = 0.005
    var simplexSpeed = 0.005
    var simplexWeight = 0.0
    var nearScreenEdgeAccel = 0.0
    var gravityWeight = 0.0
    var repulsionWeight = 0.0
    var contourAttraction = 0.0
    var contourAttractionReach = 20.0
    var contourAttractionNormalised = true
    var contourAccel = 0.0
    var randomAccel = 0.0
    var totalVelWeight = 1.0
    var contour: ShapeContour? = Circle(program.width / 2.0, program.height / 2.0, 300.0).contour
    var colorBufferShadow: ColorBufferShadow? = null
    var colorOffset = ColorRGBa.TRANSPARENT
    var particleWidth = 1
    var varyParticleWidth = false

    fun resetParameters(
        friction: Double = 1.0,
        simplexSeed: Int = 0,
        simplexScale: Double = 0.005,
        simplexSpeed: Double = 0.005,
        simplexWeight: Double = 0.0,
        nearScreenEdgeAccel: Double = 0.0,
        gravityWeight: Double = 0.0,
        repulsionWeight: Double = 0.0,
        contourAttraction: Double = 0.0,
        contourAttractionReach: Double = 20.0,
        contourAttractionNormalised: Boolean = true,
        contourAccel: Double = 0.0,
        randomAccel: Double = 0.0,
        totalVelWeight: Double = 1.0,
        contour: ShapeContour? = null,
        colorBufferShadow: ColorBufferShadow? = null,
        colorOffset: ColorRGBa = ColorRGBa.TRANSPARENT,
        particleWidth: Int = 1,
        varyParticleWidth: Boolean = false
    ) {
        this.friction = friction
        this.simplexSeed = simplexSeed
        this.simplexScale = simplexScale
        this.simplexSpeed = simplexSpeed
        this.simplexWeight = simplexWeight
        this.nearScreenEdgeAccel = nearScreenEdgeAccel
        this.gravityWeight = gravityWeight
        this.repulsionWeight = repulsionWeight
        this.contourAttraction = contourAttraction
        this.contourAttractionReach = contourAttractionReach
        this.contourAttractionNormalised = contourAttractionNormalised
        this.contourAccel = contourAccel
        this.randomAccel = randomAccel
        this.totalVelWeight = totalVelWeight
        this.contour = contour
        this.colorBufferShadow = colorBufferShadow
        this.colorOffset = colorOffset
        this.particleWidth = particleWidth
        this.varyParticleWidth = varyParticleWidth
    }

    fun update(frameNumber: Int) {
        //update average
        avgPos.set(particles.fold(MutableVector(0.0, 0.0)) { acc, particle -> acc + particle.pos }
                / particles.size.toDouble())

        //update grid
        particleGrid.forEach { it.forEach { it.clear() } }
        for (particle in particles) {
            val gridX = (particle.pos.x / program.width * GRID_ONSCREEN_WIDTH + GRID_OFFSCREEN_MARGIN).toInt()
            val gridY = (particle.pos.y / program.height * GRID_ONSCREEN_HEIGHT + GRID_OFFSCREEN_MARGIN).toInt()
            if (gridX < 0 || gridX >= GRID_TOTAL_WIDTH || gridY < 0 || gridY >= GRID_TOTAL_HEIGHT) continue
            particleGrid[gridY][gridX].add(particle)
        }

        //update particles
        particles.forEach { updateParticle(it, frameNumber) }
    }

    fun updateParticle(particle: Particle, frameNumber: Int) {
        //friction
        particle.vel.scale(friction)

        //acceleration from simplex noise
        if (simplexWeight != 0.0) {
            particle.vel.add(MutableVector(gradient3D(
                noise = simplex4D,
                seed = simplexSeed,
                x = simplexScale * particle.pos.x,
                y = simplexScale * particle.pos.y,
                z = simplexSpeed * frameNumber.toDouble()
            ).xy) * simplexWeight)
        }

        //avoid going offscreen
        when {
            particle.pos.x > program.width - GRID_SQUARE_SIZE*GRID_OFFSCREEN_MARGIN
                -> particle.vel.x -= nearScreenEdgeAccel
            particle.pos.x < GRID_SQUARE_SIZE*GRID_OFFSCREEN_MARGIN
                -> particle.vel.x += nearScreenEdgeAccel
            particle.pos.y > program.height - GRID_SQUARE_SIZE*GRID_OFFSCREEN_MARGIN
                -> particle.vel.y -= nearScreenEdgeAccel
            particle.pos.y < GRID_SQUARE_SIZE*GRID_OFFSCREEN_MARGIN
                -> particle.vel.y += nearScreenEdgeAccel
        }

        //bounce back to the screen
        when {
            particle.pos.x > program.width + GRID_SQUARE_SIZE*GRID_OFFSCREEN_MARGIN
                -> particle.vel.x = -particle.vel.x.absoluteValue
            particle.pos.x < -GRID_SQUARE_SIZE*GRID_OFFSCREEN_MARGIN
                -> particle.vel.x = particle.vel.x.absoluteValue
            particle.pos.y > program.height + GRID_SQUARE_SIZE*GRID_OFFSCREEN_MARGIN
                -> particle.vel.y = -particle.vel.y.absoluteValue
            particle.pos.y < -GRID_SQUARE_SIZE*GRID_OFFSCREEN_MARGIN
                -> particle.vel.y = particle.vel.y.absoluteValue
        }

        //gravity
        particle.vel.add((avgPos - particle.pos) * gravityWeight)

        //repulsion from nearby particles
        if (repulsionWeight != 0.0) {
            val gridX = (particle.pos.x / program.width * GRID_ONSCREEN_WIDTH + GRID_OFFSCREEN_MARGIN).toInt()
            val gridY = (particle.pos.y / program.height * GRID_ONSCREEN_HEIGHT + GRID_OFFSCREEN_MARGIN).toInt()
            if (gridX in 0 until GRID_TOTAL_WIDTH && gridY in 0 until GRID_TOTAL_HEIGHT) {
                for (nearbyParticle in particleGrid[gridY][gridX]) {
                    val s = nearbyParticle.pos - particle.pos
                    particle.vel.add(s * -repulsionWeight)
                }
            }
        }

        contour?.let { contour ->
            //attraction to contour
            if (contourAttraction != 0.0) {
                val s = contour.nearestPatch(particle.pos.toVector2()).position - particle.pos
                if (s.magSq() > contourAttractionReach.pow(2)) return@let
                if (contourAttractionNormalised) s.normalize()
                particle.vel.add(s * contourAttraction)
            }

            //acceleration from contour
            if (contourAccel != 0.0) {
                val nearestPoint = contour.nearest(particle.pos.toVector2())
                val v = nearestPoint.segment.derivative(nearestPoint.segmentT).normalized
                particle.vel.add(v * contourAccel)
            }
        }

        //random acceleration
        if (randomAccel != 0.0) {
            particle.vel.add(Vector2.uniformRing(0.0, randomAccel))
        }

        particle.pos.add(particle.vel * totalVelWeight)
    }

    fun draw() {
        program.drawer.points {
            colorBufferShadow.let {colorBufferShadow ->
                if (colorBufferShadow != null) {
                    if (varyParticleWidth) {
                        for (particle in particles) {
                            drawColouredParticle(this, particle, Random.int(1, particleWidth), colorBufferShadow)
                        }
                    }
                    else {
                        for (particle in particles) {
                            drawColouredParticle(this, particle, particleWidth, colorBufferShadow)
                        }
                    }
                }
                else {
                    fill = ColorRGBa.WHITE
                    if (varyParticleWidth) {
                        for (particle in particles) {
                            drawParticle(this, particle, Random.int(1, particleWidth))
                        }
                    }
                    else {
                        for (particle in particles) {
                            drawParticle(this, particle, particleWidth)
                        }
                    }
                }
            }
        }
    }

    private fun drawParticle(pointBatchBuilder: PointBatchBuilder, particle: Particle, size: Int) {
        for (x in 0 until size) {
            for (y in 0 until size) {
                pointBatchBuilder.point(particle.pos.x + x, particle.pos.y + y)
            }
        }
    }

    private fun drawColouredParticle(pointBatchBuilder: PointBatchBuilder, particle: Particle, size: Int,
                                     colorBufferShadow: ColorBufferShadow) {
        for (x in 0 until size) {
            for (y in 0 until size) {
                if     (particle.pos.x >= 0.0 && particle.pos.x < 1920.0 &&
                        particle.pos.y >= 0.0 && particle.pos.y < 1080.0) {
                    pointBatchBuilder.fill =
                        colorBufferShadow[particle.pos.x.toInt(), particle.pos.y.toInt()] + colorOffset
                }
                pointBatchBuilder.point(particle.pos.x + x, particle.pos.y + y)
            }
        }
    }
}

