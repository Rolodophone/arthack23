import org.openrndr.math.Vector2
import kotlin.math.sqrt

private val tmpVec = MutableVector()

class MutableVector(var x: Double, var y: Double) {
	constructor() : this(0.0, 0.0)
	constructor(vector2: Vector2) : this(vector2.x, vector2.y)

	fun set(x: Double, y: Double) {
		this.x = x
		this.y = y
	}

	fun set(other: MutableVector) {
		x = other.x
		y = other.y
	}

	fun set(vector2: Vector2) {
		x = vector2.x
		y = vector2.y
	}

	fun add(other: MutableVector) {
		x += other.x
		y += other.y
	}

	fun add(x: Double, y: Double) {
		this.x += x
		this.y += y
	}

	fun add(vector2: Vector2) {
		x += vector2.x
		y += vector2.y
	}

	fun sub(other: MutableVector) {
		x -= other.x
		y -= other.y
	}

	fun sub(x: Double, y: Double) {
		this.x -= x
		this.y -= y
	}

	fun sub(vector2: Vector2) {
		x -= vector2.x
		y -= vector2.y
	}

	fun scale(scalar: Double) {
		x *= scalar
		y *= scalar
	}

	fun neg() {
		x = -x
		y = -y
	}

	fun mag() = sqrt(x*x + y*y)

	fun magSq() = x*x + y*y

	fun normalize() {
		val mag = mag()
		x /= mag
		y /= mag
	}

	fun rotate90() {
		val tmp = x
		x = -y
		y = tmp
	}

	fun rotate270() {
		val tmp = x
		x = y
		y = -tmp
	}

	operator fun plus(other: MutableVector): MutableVector {
		tmpVec.set(x + other.x, y + other.y)
		return tmpVec
	}

	operator fun plus(other: Vector2): MutableVector {
		tmpVec.set(x + other.x, y + other.y)
		return tmpVec
	}

	operator fun minus(other: MutableVector): MutableVector {
		tmpVec.set(x - other.x, y - other.y)
		return tmpVec
	}

	operator fun minus(other: Vector2): MutableVector {
		tmpVec.set(x - other.x, y - other.y)
		return tmpVec
	}

	operator fun times(scalar: Double): MutableVector {
		tmpVec.set(x * scalar, y * scalar)
		return tmpVec
	}

	operator fun div(scalar: Double): MutableVector {
		tmpVec.set(x / scalar, y / scalar)
		return tmpVec
	}

	operator fun unaryMinus(): MutableVector {
		tmpVec.set(-x, -y)
		return tmpVec
	}

	override fun toString() = "($x, $y)"

	fun toVector2() = Vector2(x, y)
}

operator fun Vector2.minus(other: MutableVector): MutableVector {
	tmpVec.set(x - other.x, y - other.y)
	return tmpVec
}