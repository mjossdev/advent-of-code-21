private enum class Direction {
    UP, DOWN, FORWARD
}

fun main() {
    data class Command(val direction: Direction, val distance: Int)

    fun part1(input: List<Command>): Long {
        var position = 0L
        var depth = 0L
        input.forEach {
            when (it.direction) {
                Direction.UP -> depth -= it.distance
                Direction.DOWN -> depth += it.distance
                Direction.FORWARD -> position += it.distance
            }
        }
        return position * depth
    }

    fun part2(input: List<Command>): Long {
        var position = 0L
        var depth = 0L
        var aim = 0L
        input.forEach {
            when (it.direction) {
                Direction.UP -> aim -= it.distance
                Direction.DOWN -> aim += it.distance
                Direction.FORWARD -> {
                    position += it.distance
                    depth += aim * it.distance
                }
            }
        }
        return position * depth
    }

    val input = readInput("Day02").map {
        val (direction, distance) = it.split(' ')
        Command(Direction.valueOf(direction.uppercase()), distance.toInt())
    }

    println(part1(input))
    println(part2(input))
}
