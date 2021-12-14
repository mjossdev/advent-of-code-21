import java.util.function.BiFunction

fun main() {
    fun readPuzzleInput(name: String): Pair<String, Map<Pair<Char, Char>, Char>> {
        val input = readInput(name)
        val start = input[0]
        val rules = input.subList(2, input.size).associate {
            val (pair, insertion) = it.split(" -> ")
            Pair(pair[0], pair[1]) to insertion.single()
        }
        return Pair(start, rules)
    }

    fun calculateDifference(start: String, rules: Map<Pair<Char, Char>, Char>, steps: Int): Long {
        val charCounts = start
            .groupingBy { it }
            .eachCount()
            .mapValues { it.value.toLong() }
            .toMutableMap()
        var pairCounts = start
            .zipWithNext()
            .groupingBy { it }
            .eachCount()
            .mapValues { it.value.toLong() }

        repeat(steps) {
            val newCounts = mutableMapOf<Pair<Char, Char>, Long>()
            pairCounts.forEach { (pair, count) ->
                val countFunction = BiFunction<Any, Long?, Long> { _, v -> (v ?: 0) + count }
                val midChar = rules[pair]
                if (midChar != null) {
                    newCounts.compute(Pair(pair.first, midChar), countFunction)
                    newCounts.compute(Pair(midChar, pair.second), countFunction)
                    charCounts.compute(midChar, countFunction)
                } else {
                    newCounts.compute(pair, countFunction)
                }
            }
            pairCounts = newCounts
        }

        return charCounts.maxOf { it.value } - charCounts.minOf { it.value }
    }

    fun part1(start: String, rules: Map<Pair<Char, Char>, Char>): Long = calculateDifference(start, rules, 10)

    fun part2(start: String, rules: Map<Pair<Char, Char>, Char>): Long = calculateDifference(start, rules, 40)

    val testInput = readPuzzleInput("Day14_test")
    val input = readPuzzleInput("Day14")

    check(part1(testInput.first, testInput.second) == 1588L)
    println(part1(input.first, input.second))

    check(part2(testInput.first, testInput.second) == 2188189693529)
    println(part2(input.first, input.second))
}
