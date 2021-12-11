fun main() {
    fun simulateStep(grid: List<IntArray>) {
        fun incrementPowerLevel(row: Int, col: Int) {
            val power = ++grid[row][col]
            if (power != 10) return
            for (r in row - 1..row + 1 ) {
                if (r !in grid.indices) continue
                for (c in col - 1..col + 1) {
                    if (c !in grid[r].indices || c == col && r == row) continue
                    incrementPowerLevel(r, c)
                }
            }
        }

        for (row in grid) {
            row.forEachIndexed { colIndex, powerLevel ->
                if (powerLevel > 9) {
                    row[colIndex] = 0
                }
            }
        }
        grid.forEachIndexed { rowIndex, row -> row.indices.forEach { incrementPowerLevel(rowIndex, it) } }
    }

    fun countFlashes(input: List<List<Int>>, steps: Int): Int {
        val grid = input.map { it.toIntArray() }

        var flashes = 0
        repeat(steps) {
            simulateStep(grid)
            flashes += grid.sumOf { r -> r.count { it > 9 } }
        }
        return flashes
    }

    fun findSynchronizationStep(input: List<List<Int>>): Int {
        val grid = input.map { it.toIntArray() }
        var step = 0
        while (grid.any { r -> r.any { it <= 9 } }) {
            simulateStep(grid)
            ++step
        }
        return step
    }

    fun part1(input: List<List<Int>>) = countFlashes(input, 100)
    fun part2(input: List<List<Int>>) = findSynchronizationStep(input)

    val testInput = readInputAsIntGrid("Day11_test")
    val input = readInputAsIntGrid("Day11")

    check(part1(testInput) == 1656)
    println(part1(input))

    check(part2(testInput) == 195)
    println(part2(input))
}
