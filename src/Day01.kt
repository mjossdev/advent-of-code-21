fun main() {
    fun part1(input: List<Int>): Int = input.zipWithNext().count { it.first < it.second }
    fun part2(input: List<Int>): Int = part1(input.windowed(3) { it.sum() })

    val input = readInput("Day01").map { it.toInt() }
    println(part1(input))
    println(part2(input))
}
