fun main() {
    data class Coordinate(val row: Int, val col: Int)

    operator fun <T> List<List<T>>.get(coordinate: Coordinate): T = this[coordinate.row][coordinate.col]

    fun getLowPoints(heightmap: List<List<Int>>): List<Coordinate> = buildList {
        heightmap.forEachIndexed { rowIndex, row ->
            row.forEachIndexed { colIndex, height ->
                val rowRange = rowIndex - 1..rowIndex + 1 step 2
                val colRange = colIndex - 1..colIndex + 1 step 2
                if (colRange.all { height < row.getOrElse(it) { 10 } } &&
                    rowRange.all { height < (heightmap.getOrNull(it)?.getOrNull(colIndex) ?: (10)) }) {
                    add(Coordinate(rowIndex, colIndex))
                }
            }
        }
    }

    fun collectBasin(heightmap: List<List<Int>>, coordinate: Coordinate, target: MutableSet<Coordinate>) {
        if (heightmap[coordinate] == 9 || coordinate in target) {
            return
        }
        target.add(coordinate)
        val row = heightmap[coordinate.row]
        if (coordinate.row > 0) {
            collectBasin(heightmap, coordinate.copy(row = coordinate.row - 1), target)
        }
        if (coordinate.row < heightmap.lastIndex) {
            collectBasin(heightmap, coordinate.copy(row = coordinate.row + 1), target)
        }
        if (coordinate.col > 0) {
            collectBasin(heightmap, coordinate.copy(col = coordinate.col - 1), target)
        }
        if (coordinate.col < row.lastIndex) {
            collectBasin(heightmap, coordinate.copy(col = coordinate.col + 1), target)
        }
    }

    fun getBasins(heightmap: List<List<Int>>): List<Set<Coordinate>> =
        getLowPoints(heightmap).map {
            val basin = mutableSetOf<Coordinate>()
            collectBasin(heightmap, it, basin)
            basin
        }

    fun part1(input: List<List<Int>>): Int =
        getLowPoints(input).asSequence().map { input[it.row][it.col] }.sumOf { it + 1 }

    fun part2(input: List<List<Int>>): Int =
        getBasins(input).asSequence().map { it.size }.sortedDescending().take(3).reduce(Int::times)

    val testInput = readInputAsIntGrid("Day09_test")
    val input = readInputAsIntGrid("Day09")

    check(part1(testInput) == 15)
    println(part1(input))

    check(part2(testInput) == 1134)
    println(part2(input))
}
