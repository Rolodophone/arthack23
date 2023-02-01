import org.openrndr.math.Vector2

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

	override fun toString() = "($x, $y)"
}