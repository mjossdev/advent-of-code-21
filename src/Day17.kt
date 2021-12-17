import kotlin.math.absoluteValue

fun main() {
    fun String.toIntRange() = split("..").map { it.toInt() }.let { (start, end) -> start..end }

    data class Area(val x: IntRange, val y: IntRange)

    val inputPattern = Regex("""x=([^,]*), y=([^,]*)""")

    fun readPuzzleInput(name: String) = inputPattern.find(readInputAsString(name))!!.let {
        val (x, y) = it.destructured
        Area(x.toIntRange(), y.toIntRange())
    }

    fun part2(input: Area): Int {
        var count = 0
        val limit = listOf(input.x.first, input.x.last, input.y.first, input.y.last).map { it.absoluteValue }.max()
        for (xAccel in 0..limit) {
            for (yAccel in -limit..limit) {
                var currentXAccel = xAccel
                var currentYAccel = yAccel
                var currentX = 0
                var currentY = 0
                while (currentX < input.x.first || currentY > input.y.last) {
                    if (currentXAccel == 0 && currentX !in input.x) {
                        break
                    }
                    currentX += currentXAccel
                    currentY += currentYAccel
                    if (currentXAccel > 0) {
                        --currentXAccel
                    }
                    --currentYAccel
                }
                if (currentX in input.x && currentY in input.y) {
                    ++count
                }
            }
        }
        return count
    }

    fun part1(input: Area): Int {
        var peak = 0
        for (xAccel in 0..input.x.last) {
            for (yAccel in 0..input.x.last) {
                var currentXAccel = xAccel
                var currentYAccel = yAccel
                var currentX = 0
                var currentY = 0
                var peakOfThisAccel = 0
                while (currentX < input.x.first || currentY > input.y.last) {
                    if (currentXAccel == 0 && currentX !in input.x) {
                        break
                    }
                    currentX += currentXAccel
                    currentY += currentYAccel
                    if (currentY > peakOfThisAccel) {
                        peakOfThisAccel = currentY
                    }
                    if (currentXAccel > 0) {
                        --currentXAccel
                    }
                    --currentYAccel
                }
                if (currentX in input.x && currentY in input.y && peak < peakOfThisAccel) {
                    peak = peakOfThisAccel
                }
            }
        }
        return peak
    }

    val testInput = readPuzzleInput("Day17_test")
    val input = readPuzzleInput("Day17")

    check(part1(testInput) == 45)
    println(part1(input))

    check(part2(testInput) == 112)
    println(part2(input))
}
