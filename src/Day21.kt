import kotlin.math.max

fun main() {
    data class GameState(val pos1: Int, val pos2: Int, val score1: Int = 0, val score2: Int = 0)

    fun getPos(line: String) = line.split(' ').last().toInt() - 1

    fun readPuzzleInput(name: String): GameState {
        val (line1, line2) = readInput(name)
        return GameState(getPos(line1), getPos(line2))
    }

    class PracticeDice(val size: Int) {
        private var current = 0
        var diceRolls = 0
            private set

        fun roll(): Int {
            if (current == size) {
                current = 0
            }
            ++diceRolls
            return ++current
        }

        fun rollMultiple(times: Int): Int {
            var sum = 0
            repeat(times) {
                sum += roll()
            }
            return sum
        }
    }

    fun getPossibleRolls(dice: Int = 3): List<Int> {
        val range = 1..3
        return if (dice == 1) {
            range.toList()
        } else {
            getPossibleRolls(dice - 1).flatMap { r ->
                range.map { r + it }
            }
        }
    }

    fun part1(input: GameState): Int {
        val dice = PracticeDice(100)
        var (pos1, pos2) = input
        var score1 = 0
        var score2 = 0
        while (true) {
            pos1 = (pos1 + dice.rollMultiple(3)) % 10
            score1 += pos1 + 1
            if (score1 >= 1000) {
                return score2 * dice.diceRolls
            }
            pos2 = (pos2 + dice.rollMultiple(3)) % 10
            score2 += pos2 + 1
            if (score2 >= 1000) {
                return score1 * dice.diceRolls
            }
        }
    }

    fun part2(input: GameState): Long {
        val rollCounts = getPossibleRolls().groupingBy { it }.eachCount()
        val win = 21

        var gameStateUniverseCounts = mapOf(input to 1L)
        var player1Wins = 0L
        var player2Wins = 0L
        while (gameStateUniverseCounts.isNotEmpty()) {
            gameStateUniverseCounts = buildMap {
                for ((state, universes) in gameStateUniverseCounts) {
                    for ((roll, count) in rollCounts) {
                        val pos1 = (state.pos1 + roll) % 10
                        val score1 = state.score1 + pos1 + 1
                        compute(state.copy(pos1 = pos1, score1 = score1)) { _, c ->
                            (c ?: 0L) + universes * count
                        }
                    }
                }
            }
            val p1WinningStates = gameStateUniverseCounts.filterKeys { it.score1 >= win }
            player1Wins += p1WinningStates.asSequence().sumOf { it.value }
            gameStateUniverseCounts -= p1WinningStates.keys

            gameStateUniverseCounts = buildMap {
                for ((state, universes) in gameStateUniverseCounts) {
                    for ((roll, count) in rollCounts) {
                        val pos2 = (state.pos2 + roll) % 10
                        val score2 = state.score2 + pos2 + 1
                        compute(state.copy(pos2 = pos2, score2 = score2)) { _, c ->
                            (c ?: 0L) + universes * count
                        }
                    }
                }
            }
            val p2WinningStates = gameStateUniverseCounts.filterKeys { it.score2 >= win }
            player2Wins += p2WinningStates.asSequence().sumOf { it.value }
            gameStateUniverseCounts -= p2WinningStates.keys
        }
        return max(player1Wins, player2Wins)
    }

    val testInput = readPuzzleInput("Day21_test")
    val input = readPuzzleInput("Day21")

    check(part1(testInput) == 739785)
    println(part1(input))

    check(part2(testInput) == 444356092776315)
    println(part2(input))
}
