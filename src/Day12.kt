private const val START = "start"
private const val END = "end"

fun main() {
    fun buildCaveSystem(connections: List<Pair<String, String>>): Map<String, List<String>> {
        val connectionsPerCave = mutableMapOf<String, MutableList<String>>()
        for ((a, b) in connections) {
            connectionsPerCave.computeIfAbsent(a) { mutableListOf() }.add(b)
            connectionsPerCave.computeIfAbsent(b) { mutableListOf() }.add(a)
        }
        return connectionsPerCave
    }

    fun String.isLowerCase(): Boolean = all { it.isLowerCase() }

    fun part1(input: List<Pair<String, String>>): Int {
        val caveSystem = buildCaveSystem(input)
        val foundPaths = mutableListOf<List<String>>()

        fun findPaths(currentPath: List<String>) {
            val current = currentPath.last()
            if (current == END) {
                foundPaths.add(currentPath)
                return
            }
            for (cave in caveSystem.getValue(current)) {
                if (cave.isLowerCase() && cave in currentPath) {
                    continue
                }
                findPaths(currentPath + cave)
            }
        }

        findPaths(listOf(START))
        return foundPaths.size
    }

    fun part2(input: List<Pair<String, String>>): Int {
        val caveSystem = buildCaveSystem(input)
        val foundPaths = mutableListOf<List<String>>()

        fun findPaths(currentPath: List<String>) {
            val current = currentPath.last()
            if (currentPath.size > 1 && current == currentPath.first()) {
                return
            }
            if (current == END) {
                foundPaths.add(currentPath)
                return
            }
            val smallCaveLimit = 3 - currentPath.filter { it.isLowerCase() }.groupingBy { it }.eachCount().maxOf { it.value }
            for (cave in caveSystem.getValue(current)) {
                if (cave.isLowerCase() && currentPath.count{ it == cave } >= smallCaveLimit) {
                    continue
                }
                findPaths(currentPath + cave)
            }
        }
        findPaths(listOf(START))
        return foundPaths.size
    }

    fun readConnections(name: String) = readInput(name).map { it.split('-').let { (a, b) -> Pair(a, b) } }

    val testInput = readConnections("Day12_test")
    val input = readConnections("Day12")

    check(part1(testInput) == 10)
    println(part1(input))

    check(part2(testInput) == 36)
    println(part2(input))
}
