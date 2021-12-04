class BingoGame(private val numbers: List<Int>, participants: List<BingoParticipant>) {
    private val winners = mutableListOf<BingoParticipant>()
    private val participants = participants.toMutableList()

    fun play(): List<BingoParticipant> {
        if (winners.isNotEmpty()) {
            return winners.toList()
        }
        for (n in numbers) {
            val itr = participants.iterator()
            while (itr.hasNext()) {
                val participant = itr.next()
                participant.mark(n)
                if (participant.hasWon) {
                    winners.add(participant)
                    itr.remove()
                }
            }
            if (participants.isEmpty()) break
        }
        return winners.toList()
    }
}

class BingoParticipant(board: List<List<Int>>) {
    var score: Int = 0
        private set
    val hasWon: Boolean get() = score > 0

    private val board: List<List<BingoField>> = board.map { row ->
        row.map { BingoField(it) }
    }

    fun mark (number: Int) {
        check(score == 0)
        board.forEach { row ->
            val colIndex = row.indexOfFirst { it.number == number }
            if (colIndex > -1) {
                val field = row[colIndex]
                field.marked = true
                val hasWon = row.all { it.marked } || board.all { it[colIndex].marked }
                if (hasWon) {
                    calculateScore(number)
                }
                return
            }
        }
    }

    private fun calculateScore(lastNumber: Int) {
        score = lastNumber * board.sumOf { row ->
            row.filter { !it.marked }.sumOf { it.number }
        }
    }
}

data class BingoField (val number: Int, var marked: Boolean = false)

fun parseBoard(lines: List<String>): List<List<Int>> {
    check(lines.size == 5)
    return lines.map { l ->
        l.split(' ').filter { it.isNotBlank() }.map { it.toInt() }
    }
}

fun createBingoGame(input: List<String>): BingoGame {
    val participants = input.drop(1).chunked(5) { BingoParticipant(parseBoard(it)) }
    val numbers = input.first().split(',').map { it.toInt() }
    return BingoGame(numbers, participants)
}

fun main() {
    fun part1(input: BingoGame): Int = input.play().first().score
    fun part2(input: BingoGame): Int = input.play().last().score

    val testInput = readInputNoBlanks("Day04_test")
    val testGame = createBingoGame(testInput)
    val input = readInputNoBlanks("Day04")
    val game = createBingoGame(input)

    check(part1(testGame) == 4512)
    println(part1(game))
    check(part2(testGame) == 1924)
    println(part2(game))
}
