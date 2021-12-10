fun main() {
    val charMap = mapOf('(' to ')', '[' to ']', '{' to '}', '<' to '>')
    val scores = mapOf(')' to 3, ']' to 57, '}' to 1197, '>' to 25137)

    fun findCorruptedChar(line: String): Char? {
        val closingChars = ArrayDeque<Char>()
        for (c in line) {
            if (charMap.containsKey(c)) {
                closingChars.addLast(charMap.getValue(c))
            } else {
                val expectedChar = closingChars.removeLastOrNull()
                if (c != expectedChar) {
                    return c
                }
            }
        }
        return null
    }

    fun getScore(line: String): Long {
        val stack = ArrayDeque<Char>()
        for (c in line) {
            if (charMap.containsKey(c)) {
                stack.addLast(charMap.getValue(c))
            } else {
                val expectedChar = stack.removeLastOrNull()
                if (c != expectedChar) {
                    return 0
                }
            }
        }
        var score = 0L
        while (stack.isNotEmpty()) {
            score *= 5
            val char = stack.removeLast()
            score += when (char) {
                ')' -> 1
                ']' -> 2
                '}' -> 3
                '>' -> 4
                else -> error("unexpected char in stack")
            }
        }
        return score
    }

    fun part2(input: List<String>): Long = input.map { getScore(it) }.filter { it > 0 }.sorted().let { it[it.size / 2] }
    fun part1(input: List<String>): Int = input.mapNotNull { findCorruptedChar(it) }.sumOf { scores.getValue(it) }

    val testInput = readInput("Day10_test")
    val input = readInput("Day10")

    check(part1(testInput) == 26397)
    println(part1(input))

    check(part2(testInput) == 288957L)
    println(part2(input))
}
