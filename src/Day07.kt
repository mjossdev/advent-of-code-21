import kotlin.math.abs

fun main() {
    fun part1(input: List<Int>): Int = input.minMaxRange().minOf { pos -> input.sumOf { abs(it - pos) } }

    fun part2(input: List<Int>): Int =
        input.minMaxRange()
            .minOf { pos ->
                input.sumOf {
                    val n = abs(pos - it)
                    // Gauss Sum
                    (n * n + n) / 2
                }
            }

    val testInput = readInputAsInts("Day07_test")
    val input = readInputAsInts("Day07")

    check(part1(testInput) == 37)
    println(part1(input))

    check(part2(testInput) == 168)
    println(part2(input))
}
