import java.util.*

private enum class Segment {
    TOP, TOP_LEFT, TOP_RIGHT, MIDDLE, BOTTOM_LEFT, BOTTOM_RIGHT, BOTTOM;

    companion object {
        fun allExcept(vararg segments: Segment): Set<Segment> = EnumSet.allOf(Segment::class.java).apply {
            removeAll(segments.toSet())
        }
    }
}

private enum class Digit(val segments: Set<Segment>) {
    ZERO(Segment.allExcept(Segment.MIDDLE)),
    ONE(EnumSet.of(Segment.TOP_RIGHT, Segment.BOTTOM_RIGHT)),
    TWO(Segment.allExcept(Segment.TOP_LEFT, Segment.BOTTOM_RIGHT)),
    THREE(Segment.allExcept(Segment.TOP_LEFT, Segment.BOTTOM_LEFT)),
    FOUR(Segment.allExcept(Segment.TOP, Segment.BOTTOM_LEFT, Segment.BOTTOM)),
    FIVE(Segment.allExcept(Segment.TOP_RIGHT, Segment.BOTTOM_LEFT)),
    SIX(Segment.allExcept(Segment.TOP_RIGHT)),
    SEVEN(EnumSet.of(Segment.TOP, Segment.TOP_RIGHT, Segment.BOTTOM_RIGHT)),
    EIGHT(EnumSet.allOf(Segment::class.java)),
    NINE(Segment.allExcept(Segment.BOTTOM_LEFT));

    companion object {
        private val digitBySegments: Map<Set<Segment>, Digit> = values().associateBy { it.segments }
        private val digitsByNumberOfSegments: Map<Int, List<Digit>> = values().groupBy { it.segments.size }

        fun valueOf(segments: Set<Segment>): Digit = digitBySegments.getValue(segments)
        fun getDigitsWithNumberOfSegments(n: Int): List<Digit> = digitsByNumberOfSegments.getValue(n)
    }
}

private fun List<Digit>.toInt(): Int {
    var value = 0
    var factor = 1
    asReversed().forEach {
        value += factor * it.ordinal
        factor *= 10
    }
    return value
}

private fun Set<Segment>.toDigit(): Digit = Digit.valueOf(this)

private fun resolvePatternDigits(patterns: Iterable<Set<Char>>): Map<Set<Char>, Digit> {
    val letters = 'a'..'g'

    operator fun <T> Array<T>.get(c: Char) = this[c.code - letters.first.code]

    val map = Array<MutableSet<Segment>>(7) { EnumSet.allOf(Segment::class.java) }
    for (pattern in patterns) {
        val possibleSegmentGroups = Digit.getDigitsWithNumberOfSegments(pattern.size).map { it.segments }
        val union = possibleSegmentGroups.unionAll()
        val intersection = possibleSegmentGroups.intersectAll()
        for (c in letters) {
            val set = map[c]
            if (c in pattern) {
                set.retainAll(union)
            } else {
                set.removeAll(intersection)
            }
        }
        for (c in letters) {
            val set = map[c]
            if (set.size == 1) {
                letters.filter { it != c }.forEach { map[it].removeAll(set) }
            }
        }
    }
    return patterns.associateWith { p -> p.map { map[it].single() }.toSet().toDigit() }
}

fun main() {
    val uniqueSegmentSizes = intArrayOf(2, 3, 4, 7)

    data class Entry(val patterns: List<Set<Char>>, val outputs: List<Set<Char>>)

    fun String.toEntry(): Entry {
        val (patterns, outputs) = split(" | ").map {
            it.split(' ').map { s -> s.toSet() }
        }
        return Entry(patterns, outputs)
    }

    fun part1(input: List<Entry>) = input.sumOf { e ->
        e.outputs.count { it.size in uniqueSegmentSizes }
    }

    fun part2(input: List<Entry>): Int = input.sumOf { entry ->
        val patternMap = resolvePatternDigits(entry.patterns)
        entry.outputs.map { patternMap.getValue(it) }.toInt()
    }

    val testInput = readInput("Day08_test").map { it.toEntry() }
    val input = readInput("Day08").map { it.toEntry() }

    check(part1(testInput) == 26)
    println(part1(input))

    check(part2(testInput) == 61229)
    println(part2(input))
}
