import java.io.File
import java.math.BigInteger
import java.security.MessageDigest
import kotlin.math.sign

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = File("data", "$name.txt").readLines()

fun readInputNoBlanks(name: String) = readInput(name).filter { it.isNotBlank() }

fun readInputAsString(name: String) = File("data", "$name.txt").readText().trim()

fun readInputAsInts(name: String, delimiter: String = ",") = readInputAsString(name).split(delimiter).map { it.toInt() }

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

fun <T : Comparable<T>> Iterable<T>.min() = this.minOf { it }
fun <T : Comparable<T>> Iterable<T>.max() = this.maxOf { it }

fun Iterable<Int>.minMaxRange() = min()..max()

fun <T> Iterable<Set<T>>.unionAll(): Set<T> {
    val target = mutableSetOf<T>()
    forEach { target.addAll(it) }
    return target
}

fun <T> Iterable<Set<T>>.intersectAll(): Set<T> = iterator().let {
    if (it.hasNext()) {
        val target = it.next().toMutableSet()
        it.forEachRemaining { s -> target.retainAll(s) }
        target
    } else {
        emptySet()
    }
}


/**
 * Converts string to md5 hash.
 */
fun String.md5(): String = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray())).toString(16)
