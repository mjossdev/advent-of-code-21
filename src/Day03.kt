fun main() {
    fun part1(input: List<String>): Int {
        val oneCounts = IntArray(input[0].length)
        input.forEach {
            it.forEachIndexed { index, c ->
                oneCounts[index] += c.digitToInt()
            }
        }
        val mostFrequentDigits = oneCounts.map {
            if (it > input.size / 2) '1' else '0'
        }.joinToString("")
        val gamma = mostFrequentDigits.toInt(2)
        val lowerBitsMask = (0.inv() ushr (Int.SIZE_BITS - mostFrequentDigits.length))
        val epsilon = gamma.inv() and lowerBitsMask
        return gamma * epsilon
    }
    fun part2(input: List<String>): Int {
        val digits = input[0].length

        val oxygenRatingCandidates = input.toMutableList()
        for (i in 0 until digits) {
            val oneCount = oxygenRatingCandidates.count { it[i] == '1' }
            val zeroCount = oxygenRatingCandidates.size - oneCount
            val expectedDigit = if (oneCount >= zeroCount) '1' else '0'
            oxygenRatingCandidates.removeIf {
                it[i] != expectedDigit
            }
            if (oxygenRatingCandidates.size == 1) {
                break
            }
        }
        val oxygenRating = oxygenRatingCandidates[0].toInt(2)

        val co2RatingCandidates = input.toMutableList()
        for (i in 0 until digits) {
            val oneCount = co2RatingCandidates.count { it[i] == '1' }
            val zeroCount = co2RatingCandidates.size - oneCount
            val expectedDigit = if (oneCount < zeroCount) '1' else '0'
            co2RatingCandidates.removeIf {
                it[i] != expectedDigit
            }
            if (co2RatingCandidates.size == 1) {
                break
            }
        }
        val co2Rating = co2RatingCandidates[0].toInt(2)

        return oxygenRating * co2Rating
    }

    val input = readInput("Day03")
    println(part1(input))
    println(part2(input))
}
