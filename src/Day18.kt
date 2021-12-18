import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive

private sealed class ReduceResult(val number: SnailfishNumber)

private class UnchangedResult(number: SnailfishNumber) : ReduceResult(number)

private class ChangedResult(number: SnailfishNumber) : ReduceResult(number)

private class ExplodeResult(number: SnailfishNumber, val left: Int?, val right: Int?) : ReduceResult(number) {
    fun withNumber(number: SnailfishNumber) = ExplodeResult(number, left, right)
}

private class SnailfishNumber {
    val value: Any

    constructor(number: Int) {
        value = number
    }

    constructor(left: SnailfishNumber, right: SnailfishNumber) {
        value = Pair(left, right)
    }

    @Suppress("UNCHECKED_CAST")
    fun asPair(): Pair<SnailfishNumber, SnailfishNumber> {
        check(value is Pair<*, *>)
        return value as Pair<SnailfishNumber, SnailfishNumber>
    }

    fun reduce(): SnailfishNumber {
        var current = this
        do {
            var reduceResult = current.explode()
            if (reduceResult is UnchangedResult) {
                reduceResult = current.split()
            }
            current = reduceResult.number
        } while (reduceResult !is UnchangedResult)
        return current
    }

    private fun split(): ReduceResult {
        when (value) {
            is Int -> {
                if (value > 9) {
                    val left = value / 2
                    val right = (value + 1) / 2
                    val number = SnailfishNumber(SnailfishNumber(left), SnailfishNumber(right))
                    return ChangedResult(number)
                }
            }
            is Pair<*, *> -> {
                val (left, right) = asPair()
                val leftResult = left.split()
                if (leftResult is ChangedResult) {
                    return ChangedResult(SnailfishNumber(leftResult.number, right))
                }
                val rightResult = right.split()
                if (rightResult is ChangedResult) {
                    return ChangedResult(SnailfishNumber(left, rightResult.number))
                }
            }
        }
        return UnchangedResult(this)
    }

    private fun explode(level: Int = 0): ReduceResult {
        if (value is Pair<*, *>) {
            val (left, right) = asPair()
            if (level > 3) {
                val number = SnailfishNumber(0)
                return ExplodeResult(number, left.value as Int, right.value as Int)
            }
            val leftResult = left.explode(level + 1)
            if (leftResult is ExplodeResult) {
                leftResult.right?.also {
                    val addResult = right.addLeftMost(it)
                    if (addResult is ChangedResult) {
                        return ExplodeResult(
                            SnailfishNumber(leftResult.number, addResult.number),
                            leftResult.left,
                            null
                        )
                    }
                }
                return leftResult.withNumber(SnailfishNumber(leftResult.number, right))
            }
            val rightResult = right.explode(level + 1)
            if (rightResult is ExplodeResult) {
                rightResult.left?.also {
                    val addResult = left.addRightMost(it)
                    if (addResult is ChangedResult) {
                        return ExplodeResult(
                            SnailfishNumber(addResult.number, rightResult.number),
                            null,
                            rightResult.right
                        )
                    }
                }
                return rightResult.withNumber(SnailfishNumber(left, rightResult.number))
            }
        }
        return UnchangedResult(this)
    }

    fun addLeftMost(number: Int): ReduceResult {
        when (value) {
            is Int -> return ChangedResult(SnailfishNumber(value + number))
            else -> {
                val (left, right) = asPair()
                val leftResult = left.addLeftMost(number)
                if (leftResult is ChangedResult) {
                    return ChangedResult(SnailfishNumber(leftResult.number, right))
                }
                val rightResult = right.addLeftMost(number)
                if (rightResult is ChangedResult) {
                    return ChangedResult(SnailfishNumber(left, rightResult.number))
                }
                return UnchangedResult(this)
            }
        }
    }

    fun addRightMost(number: Int): ReduceResult {
        when (value) {
            is Int -> return ChangedResult(SnailfishNumber(value + number))
            else -> {
                val (left, right) = asPair()
                val rightResult = right.addRightMost(number)
                if (rightResult is ChangedResult) {
                    return ChangedResult(SnailfishNumber(left, rightResult.number))
                }
                val leftResult = left.addRightMost(number)
                if (leftResult is ChangedResult) {
                    return ChangedResult(SnailfishNumber(leftResult.number, right))
                }
                return UnchangedResult(this)
            }
        }
    }

    operator fun plus(other: SnailfishNumber) = SnailfishNumber(this, other).reduce()

    fun magnitude(): Int = when (value) {
        is Int -> value
        else -> {
            val (left, right) = asPair()
            3 * left.magnitude() + 2 * right.magnitude()
        }
    }

    override fun toString() = if (value is Pair<*, *>) "[${value.first},${value.second}]" else value.toString()

    companion object {
        fun fromJson(json: JsonElement): SnailfishNumber = when (json) {
            is JsonPrimitive -> SnailfishNumber(json.asInt)
            is JsonArray -> {
                check(json.size() == 2)
                val left = fromJson(json[0])
                val right = fromJson(json[1])
                SnailfishNumber(left, right)
            }
            else -> error("Unsupported type")
        }
    }
}

fun main() {
    fun readPuzzleInput(name: String): List<SnailfishNumber> {
        val gson = Gson()
        return readInput(name).map {
            val json = gson.fromJson(it, JsonElement::class.java)
            SnailfishNumber.fromJson(json)
        }
    }

    fun part1(input: List<SnailfishNumber>) = input.reduce { l, r -> (l + r) }.magnitude()

    fun part2(input: List<SnailfishNumber>): Int = input.flatMap { a ->
        input.asSequence().filter { a != it }.map {
            (a + it).magnitude()
        }
    }.max()

    val testInput = readPuzzleInput("Day18_test")
    val input = readPuzzleInput("Day18")

    check(part1(testInput) == 4140)
    println(part1(input))

    check(part2(testInput) == 3993)
    println(part2(input))
}
