import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive

private sealed interface ReduceResult {
    val node: Node
}

private data class UnchangedResult(override val node: Node) : ReduceResult

private data class ChangedResult(override val node: Node) : ReduceResult

private data class ExplodeResult(override val node: Node, val left: Int?, val right: Int?) : ReduceResult

private sealed class Node {
    fun reduce(): Node {
        var current = this
        do {
            var reduceResult = current.explode()
            if (reduceResult is UnchangedResult) {
                reduceResult = current.split()
            }
            current = reduceResult.node
        } while (reduceResult !is UnchangedResult)
        return current
    }

    abstract fun split(): ReduceResult
    abstract fun explode(level: Int = 0): ReduceResult
    abstract fun addLeftMost(number: Int): ReduceResult
    abstract fun addRightMost(number: Int): ReduceResult

    abstract val magnitude: Int
}

private data class NumberNode(val number: Int): Node() {
    override fun split(): ReduceResult =
        if (number > 9) {
            val left = NumberNode(number / 2)
            val right = NumberNode((number + 1) / 2)
            ChangedResult(PairNode(left, right))
        } else {
            UnchangedResult(this)
        }

    override fun explode(level: Int): ReduceResult = UnchangedResult(this)

    override fun addLeftMost(number: Int): ReduceResult = add(number)

    override fun addRightMost(number: Int): ReduceResult = add(number)

    private fun add(number: Int): ChangedResult = ChangedResult(NumberNode(this.number + number))

    override val magnitude: Int get() = number

    override fun toString() = number.toString()

    companion object {
        val ZERO = NumberNode(0)
    }
}

private data class PairNode(val left: Node, val right: Node) : Node() {
    override fun split(): ReduceResult {
        val leftResult = left.split()
        if (leftResult is ChangedResult) {
            return ChangedResult(copy(left = leftResult.node))
        }
        val rightResult = right.split()
        if (rightResult is ChangedResult) {
            return ChangedResult(copy(right = rightResult.node))
        }
        return UnchangedResult(this)
    }

    override fun explode(level: Int): ReduceResult {
        if (level > 3) {
            check(left is NumberNode && right is NumberNode)
            return ExplodeResult(NumberNode.ZERO, left.number, right.number)
        }
        val leftResult = left.explode(level + 1)
        if (leftResult is ExplodeResult) {
            leftResult.right?.also {
                val addResult = right.addLeftMost(it)
                if (addResult is ChangedResult) {
                    return leftResult.copy(
                        node = PairNode(leftResult.node, addResult.node),
                        right = null
                    )
                }
            }
            val node = copy(left = leftResult.node)
            return leftResult.copy(node = node)
        }
        val rightResult = right.explode(level + 1)
        if (rightResult is ExplodeResult) {
            rightResult.left?.also {
                val addResult = left.addRightMost(it)
                if (addResult is ChangedResult) {
                    return rightResult.copy(
                        node = PairNode(addResult.node, rightResult.node),
                        left = null
                    )
                }
            }
            val node = copy(right = rightResult.node)
            return rightResult.copy(node = node)
        }
        return UnchangedResult(this)
    }

    override fun addLeftMost(number: Int): ReduceResult {
        val leftResult = left.addLeftMost(number)
        if (leftResult is ChangedResult) {
            return ChangedResult(copy(left = leftResult.node))
        }
        val rightResult = right.addLeftMost(number)
        if (rightResult is ChangedResult) {
            return ChangedResult(copy(right = rightResult.node))
        }
        return UnchangedResult(this)
    }

    override fun addRightMost(number: Int): ReduceResult {
        val rightResult = right.addRightMost(number)
        if (rightResult is ChangedResult) {
            return ChangedResult(copy(right = rightResult.node))
        }
        val leftResult = left.addRightMost(number)
        if (leftResult is ChangedResult) {
            return ChangedResult(copy(left = leftResult.node))
        }
        return UnchangedResult(this)
    }

    override val magnitude: Int get() = 3 * left.magnitude + 2 * right.magnitude

    override fun toString() = "[$left,$right]"
}

private operator fun Node.plus(other: Node) = PairNode(this, other).reduce()

private fun JsonElement.toNode(): Node = when (this) {
    is JsonPrimitive -> NumberNode(asInt)
    is JsonArray -> {
        check(size() == 2)
        PairNode(this[0].toNode(), this[1].toNode())
    }
    else -> error("Unsupported JSON type")
}

fun main() {
    fun readPuzzleInput(name: String): List<Node> {
        val gson = Gson()
        return readInput(name).map { gson.fromJson(it, JsonElement::class.java).toNode() }
    }

    fun part1(input: List<Node>) = input.reduce { l, r -> (l + r) }.magnitude

    fun part2(input: List<Node>): Int = input.flatMap { a ->
        input.asSequence().filter { a != it }.map {
            (a + it).magnitude
        }
    }.max()

    val testInput = readPuzzleInput("Day18_test")
    val input = readPuzzleInput("Day18")

    check(part1(testInput) == 4140)
    println(part1(input))

    check(part2(testInput) == 3993)
    println(part2(input))
}
