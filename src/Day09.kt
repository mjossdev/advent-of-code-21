import kotlin.math.max
import kotlin.math.min

fun main() {
    fun part1(input: List<List<Int>>): Int =
        input.flatMapIndexed { rowIndex, row ->
            row.filterIndexed { colIndex, height ->
                val rowRange = max(0, rowIndex - 1)..min(rowIndex + 1, input.lastIndex)
                val colRange = max(0, colIndex - 1)..min(colIndex + 1, row.lastIndex)
                rowRange.all { r -> r == rowIndex || height < input[r][colIndex]} && colRange.all { c -> c == colIndex || height < input[rowIndex][c]}
            }
        }.also { println(it) }.sumOf {
            it + 1
        }

    val testInput = readInput("Day09_test").map { l ->
        l.map { it.digitToInt() }
    }
    val input = readInput("Day09").map { l ->
        l.map { it.digitToInt() }
    }

    check(part1(testInput) == 15)
    println(part1(input))
}
