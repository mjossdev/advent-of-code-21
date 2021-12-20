private enum class PixelState(val symbol: Char) {
    DARK('.'), LIT('#');

    override fun toString(): String = symbol.toString()
    fun invert() = when (this) {
        DARK -> LIT
        LIT -> DARK
    }
}

fun main() {
    fun Char.toPixelState() = PixelState.values().find { this == it.symbol }!!

    class Grid<T>(val rows: Int, val cols: Int, val default: T) : Iterable<T> {
        private val storage = mutableListOf<T>()

        init {
            while (storage.size < rows * cols) {
                storage.add(default)
            }
        }

        operator fun get(row: Int, col: Int): T {
            if (row !in 0 until rows || col !in 0 until cols) {
                return default
            }
            return storage.getOrElse(index(row, col)) { default }
        }

        operator fun set(row: Int, col: Int, value: T) {
            storage[index(row, col)] = value
        }

        private fun index(row: Int, col: Int) = row * cols + col

        override fun toString(): String = buildString {
            for (row in 0 until rows) {
                for (col in 0 until cols) {
                    append(this@Grid[row, col])
                }
                appendLine()
            }
        }

        override fun iterator(): Iterator<T> = storage.iterator()
    }

    data class PuzzleInput(val lookup: List<PixelState>, val image: Grid<PixelState>)

    fun readPuzzleInput(name: String): PuzzleInput {
        val lines = readInput(name)
        val lookup = lines[0].map { it.toPixelState() }

        val image = lines.subList(2, lines.size)
        val grid = Grid(image.size, image[0].length, PixelState.DARK)
        image.forEachIndexed { rowIndex, line ->
            line.forEachIndexed { colIndex, pixel ->
                grid[rowIndex, colIndex] = pixel.toPixelState()
            }
        }
        return PuzzleInput(lookup, grid)
    }

    fun enhance(lookup: List<PixelState>, image: Grid<PixelState>): Grid<PixelState> {
        val newDefault = if (lookup[0] == PixelState.LIT) image.default.invert() else PixelState.DARK
        val newImage = Grid(image.rows + 2, image.cols + 2, newDefault)
        for (row in 0 until newImage.rows) {
            for (col in 0 until newImage.cols) {
                val index = buildString {
                    for (i in -1..1) {
                        for (j in -1..1) {
                            append(image[row + i - 1, col + j - 1].ordinal)
                        }
                    }
                }.toInt(2)
                newImage[row, col] = lookup[index]
            }
        }
        return newImage
    }

    fun part1(input: PuzzleInput): Int {
        var image = input.image
        repeat(2) {
            image = enhance(input.lookup, image)
        }
        return image.count { it == PixelState.LIT }
    }

    fun part2(input: PuzzleInput): Int {
        var image = input.image
        repeat(50) {
            image = enhance(input.lookup, image)
        }
        return image.count { it == PixelState.LIT }
    }

    val testInput = readPuzzleInput("Day20_test")
    val input = readPuzzleInput("Day20")

    check(part1(testInput) == 35)
    println(part1(input))

    check(part2(testInput) == 3351)
    println(part2(input))
}
