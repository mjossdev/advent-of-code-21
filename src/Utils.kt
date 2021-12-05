import java.io.File
import java.math.BigInteger
import java.security.MessageDigest
import kotlin.math.sign

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = File("data", "$name.txt").readLines()

fun readInputNoBlanks(name: String) = readInput(name).filter { it.isNotBlank() }

data class Point(val x: Int, val y: Int) {
    companion object {
        fun parse(s: String): Point {
            val (x, y) = s.split(',').map { it.toInt() }
            return Point(x, y)
        }
    }
}

data class Line(val start: Point, val end: Point) {
    companion object {
        fun parse(s: String): Line {
            val (start, end) = s.split(" -> ").map { Point.parse(it) }
            return Line(start, end)
        }
    }

    init {
        check(start != end)
    }

    val isHorizontal get() = start.y == end.y
    val isVertical get() = start.x == end.x
    val isDiagonal get() = !isHorizontal && !isVertical

    fun points(): Sequence<Point> = generateSequence(start) {
        if (it == end) {
            null
        } else {
            val newX = it.x + (end.x - start.x).sign
            val newY = it.y + (end.y - start.y).sign
            if (isHorizontal) {
                it.copy(x = newX)
            } else if (isVertical) {
                it.copy(y = newY)
            } else {
                Point(newX, newY)
            }
        }
    }
}

/**
 * Converts string to md5 hash.
 */
fun String.md5(): String = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray())).toString(16)
