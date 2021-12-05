fun overlaps(lines: List<Line>, includeDiagonals: Boolean): Int = lines.asSequence()
    .filter { includeDiagonals || !it.isDiagonal }
    .flatMap { it.points() }
    .groupingBy { it }
    .eachCount()
    .count { it.value > 1 }

fun main() {
    fun part1(input: List<Line>): Int = overlaps(input, false)
    fun part2(input: List<Line>): Int = overlaps(input, true)

    val testInput = readInput("Day05_test").map { Line.parse(it) }
    val input = readInput("Day05").map { Line.parse(it) }

    check(part1(testInput) == 5)
    println(part1(input))

    check(part2(testInput) == 12)
    println(part2(input))
}
