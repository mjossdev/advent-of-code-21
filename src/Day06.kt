fun main() {
    fun countFish(fishes: List<Int>, days: Int): Long {
        var fishesCounts = LongArray(9)
        fishes.forEach {
            ++fishesCounts[it]
        }
        repeat(days) {
            val newCounts = LongArray(9)
            fishesCounts.forEachIndexed { index, count ->
                if (index == 0) {
                    newCounts[8] += count
                    newCounts[6] += count
                } else {
                    newCounts[index - 1] += count
                }
            }
            fishesCounts = newCounts
        }
        return fishesCounts.sum()
    }

    fun part1(input: List<Int>): Long = countFish(input, 80)
    fun part2(input: List<Int>): Long = countFish(input, 256)

    fun readFishes(name: String) = readInput(name).flatMap { l -> l.split(',').map { it.toInt() } }

    val testInput = readFishes("Day06_test")
    val input = readFishes("Day06")

    check(part1(testInput) == 5934L)
    println(part1(input))

    check(part2(testInput) == 26984457539L)
    println(part2(input))
}
