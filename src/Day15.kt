private class AdabtablePriorityQueue<K : Comparable<K>, V> {
    private val heap = mutableListOf<EntryImpl>()
    val size get() = heap.size

    fun insert(key: K, value: V): Entry<K, V> {
        val entry = EntryImpl(key, value, size + 1)
        heap.add(entry)
        upHeap(size)
        return entry
    }

    private fun index(virtualIndex: Int) = virtualIndex - 1

    private fun upHeap(i: Int) {
        if (i == 1) return
        val entry = heap[index(i)]
        val parent = heap[index(i / 2)]
        if (entry.key < parent.key) {
            swap(i, i / 2)
            upHeap(i / 2)
        }
    }

    private fun swap(i: Int, j: Int) {
        val temp = heap[index(i)]
        heap[index(i)] = heap[index(j)]
        heap[index(j)] = temp
        heap[index(i)].index = i
        heap[index(j)].index = j
    }

    fun replaceKey(entry: Entry<K, V>, newKey: K): K {
        if (entry !is EntryImpl) {
            throw IllegalArgumentException("Entry does not belong to this queue")
        }
        val oldKey = entry.key
        entry.key = newKey
        val cmp = newKey compareTo oldKey
        if (cmp < 0) {
            upHeap(entry.index)
        } else if (cmp > 0) {
            downHeap(entry.index)
        }
        return oldKey
    }

    fun removeMin(): Entry<K, V> = remove(heap[index(1)])

    private fun downHeap(i: Int) {
        if (i * 2 > size) {
            return
        }
        val entry = heap[index(i)]
        val leftChild = heap[index(i * 2)]
        val rightChild = heap.getOrNull(index(i * 2 + 1))
        val smallerChild = if (rightChild == null || leftChild.key <= rightChild.key) {
            leftChild
        } else {
            rightChild
        }
        if (entry.key >= smallerChild.key) {
            swap(i, smallerChild.index)
            downHeap(entry.index)
        }
    }

    fun remove(entry: Entry<K, V>): Entry<K, V> {
        if (entry !is EntryImpl) {
            throw IllegalArgumentException("Entry does not belong to this queue")
        }
        val i = entry.index
        swap(i, size)
        heap.removeAt(index(size))
        if (isEmpty()) {
            return entry
        }
        val replacement = heap[index(i)]
        val cmp = replacement.key compareTo entry.key
        if (cmp < 0) {
            upHeap(i)
        } else {
            downHeap(i)
        }
        return entry
    }

    fun isEmpty() = size == 0

    interface Entry<K, V> {
        val key: K
        val value: V
    }

    private inner class EntryImpl (override var key: K, override var value: V, var index: Int) : Entry<K, V>
}

private enum class StepDirection(val deltaVertical: Int, val deltaHorizontal: Int) {
    TOP(-1, 0), RIGHT(0, 1), BOTTOM(1, 0), LEFt(0, -1);

    fun next(coordinate: Coordinate) = coordinate.let { Coordinate(it.row + deltaVertical, it.col + deltaHorizontal) }
}

private data class Coordinate(val row: Int, val col: Int)

fun main() {
    fun findPathRisk(map: List<List<Int>>): Int {
        fun isValid(coordinate: Coordinate) = coordinate.let {
            val (row, col) = it
            row >= 0 && col >= 0 && row < map.size && col < map[row].size
        }

        val risks = Array(map.size) { IntArray(map[it].size) }
        val locators = Array(map.size) { mutableListOf<AdabtablePriorityQueue.Entry<Int, Coordinate>>() }
        val queue = AdabtablePriorityQueue<Int, Coordinate>()
        map.forEachIndexed { rowIndex, row ->
            for (colIndex in row.indices) {
                risks[rowIndex][colIndex] = Int.MAX_VALUE
                locators[rowIndex].add(colIndex, queue.insert(risks[rowIndex][colIndex], Coordinate(rowIndex, colIndex)))
            }
        }
        risks[0][0] = 0
        val directions = StepDirection.values()
        while (!queue.isEmpty()) {
            val entry = queue.removeMin()
            val pos = entry.value
            if (pos.row == map.lastIndex && pos.col == map[pos.row].lastIndex) {
                return entry.key
            }
            for (direction in directions) {
                val nextPos = direction.next(pos)
                if (!isValid(nextPos)) {
                    continue
                }
                val risk = risks[pos.row][pos.col] + map[nextPos.row][nextPos.col]
                if (risk < risks[nextPos.row][nextPos.col]) {
                    risks[nextPos.row][nextPos.col] = risk
                    queue.replaceKey(locators[nextPos.row][nextPos.col], risk)
                }
            }
        }
        throw AssertionError()
    }

    fun part1(input: List<List<Int>>) = findPathRisk(input)

    fun part2(input: List<List<Int>>): Int {
        val largerMap = input.map { it.toMutableList() }.toMutableList()
        repeat(4) {
            val partSize = input[0].size
            largerMap.forEach { r ->
                r.addAll(r.takeLast(partSize).map { if (it == 9) 1 else it + 1 })
            }
        }
        repeat(4) {
            largerMap.addAll(largerMap.takeLast(input.size).map { r -> r.map { if (it == 9) 1 else it + 1  }.toMutableList() })
        }
        return findPathRisk(largerMap)
    }

    val testInput = readInputAsIntGrid("Day15_test")
    val input = readInputAsIntGrid("Day15")

    check(part1(testInput) == 40)
    println(part1(input))

    check(part2(testInput) == 315)
    println(part2(input))
}
