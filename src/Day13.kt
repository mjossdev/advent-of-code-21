private enum class Axis { X, Y; }

private data class FoldInstruction(val axis: Axis, val coordinate: Int) {
    companion object {
        fun parse(s: String): FoldInstruction {
            val equalSignIndex = s.lastIndexOf('=')
            val (axis, coordinate) = s.substring(equalSignIndex - 1).split('=')
            return FoldInstruction(Axis.valueOf(axis.uppercase()), coordinate.toInt())
        }
    }
}

fun main() {
    fun readPuzzleInput(name: String): Pair<List<Point>, List<FoldInstruction>> {
        val input = readInput(name)
        val split = input.indexOf("")
        val points = input.subList(0, split).map { Point.parse(it) }
        val foldInstructions = input.subList(split + 1, input.size).map { FoldInstruction.parse(it) }
        return Pair(points, foldInstructions)
    }

    fun fold(grid: Array<BooleanArray>, instruction: FoldInstruction): Array<BooleanArray> {
        val (axis, coordinate) = instruction
        return when (axis) {
            Axis.X -> {
                check(coordinate == grid.size / 2)
                Array(coordinate) { BooleanArray(grid[0].size) }.apply {
                    for (x in indices) {
                        val leftCol = grid[x]
                        val rightCol = grid[grid.lastIndex - x]
                        leftCol.zip(rightCol).forEachIndexed { y, (a, b) -> this[x][y] = a || b }
                    }
                }
            }
            Axis.Y -> {
                check(coordinate == grid[0].size / 2)
                Array(grid.size) { BooleanArray(coordinate) }.apply {
                    grid.forEachIndexed { x, col ->
                        for (y in 0 until coordinate) {
                            this[x][y] = col[y] || col[col.lastIndex - y]
                        }
                    }
                }
            }
        }
    }

    fun createGrid(points: List<Point>, instructions: List<FoldInstruction>): Array<BooleanArray> {
        val width = instructions.find { it.axis == Axis.X }!!.coordinate * 2 + 1
        val height = instructions.find { it.axis == Axis.Y }!!.coordinate * 2 + 1
        val grid = Array(width) { BooleanArray(height) }
        for ((x, y) in points) {
            grid[x][y] = true
        }
        return grid
    }

    fun part1(points: List<Point>, instructions: List<FoldInstruction>): Int =
        fold(createGrid(points, instructions), instructions.first()).sumOf { col -> col.count { it } }

    fun part2(points: List<Point>, instructions: List<FoldInstruction>): String {
        var grid = createGrid(points, instructions)
        for (instruction in instructions) {
            grid = fold(grid, instruction)
        }
        return buildString {
            for (y in grid[0].indices) {
                for (col in grid) {
                    append(if (col[y]) '#' else '.')
                }
                appendLine()
            }
        }
    }

    val testInput = readPuzzleInput("Day13_test")
    val input = readPuzzleInput("Day13")

    check(part1(testInput.first, testInput.second) == 17)
    println(part1(input.first, input.second))

    val expected = """
        #####
        #...#
        #...#
        #...#
        #####
        .....
        .....

    """.trimIndent()
    check(part2(testInput.first, testInput.second) == expected)
    println(part2(input.first, input.second))
}
