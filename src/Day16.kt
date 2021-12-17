private interface Packet {
    val version: Int
    val subPackets: List<Packet>
    val value: Long
}

fun main() {
    data class LiteralPacket(override val version: Int, override val value: Long) : Packet {
        override val subPackets = emptyList<Packet>()
    }

    data class SumPacket(override val version: Int, override val subPackets: List<Packet>) : Packet {
        override val value: Long = subPackets.sumOf { it.value }
    }

    data class ProductPacket(override val version: Int, override val subPackets: List<Packet>) : Packet {
        override val value: Long = subPackets.fold(1) { product, packet -> product * packet.value }
    }

    data class MinPacket(override val version: Int, override val subPackets: List<Packet>) : Packet {
        override val value: Long = subPackets.minOf { it.value }
    }

    data class MaxPacket(override val version: Int, override val subPackets: List<Packet>) : Packet {
        override val value: Long = subPackets.maxOf { it.value }
    }

    data class GreaterThanPacket(override val version: Int, override val subPackets: List<Packet>) : Packet {
        override val value: Long = if (subPackets[0].value > subPackets[1].value) 1 else 0
    }

    data class LessThanPacket(override val version: Int, override val subPackets: List<Packet>) : Packet {
        override val value: Long = if (subPackets[0].value < subPackets[1].value) 1 else 0
    }

    data class EqualToPacket(override val version: Int, override val subPackets: List<Packet>) : Packet {
        override val value: Long = if (subPackets[0].value == subPackets[1].value) 1 else 0
    }

    data class ParseResult(val packet: Packet, val rest: String)

    fun getConstructor(packetTypeId: Int): (Int, List<Packet>) -> Packet = when(packetTypeId) {
        0 -> ::SumPacket
        1 -> ::ProductPacket
        2 -> ::MinPacket
        3 -> ::MaxPacket
        5 -> ::GreaterThanPacket
        6 -> ::LessThanPacket
        7 -> ::EqualToPacket
        else -> throw IllegalArgumentException("$packetTypeId is not an operator type ID")
    }

    fun Packet.visit(action: (Packet) -> Unit) {
        action(this)
        subPackets.forEach { it.visit(action) }
    }

    fun parse(input: String): ParseResult {
        val version = input.substring(0, 3).toInt(2)
        val packetTypeId = input.substring(3, 6).toInt(2)
        return if (packetTypeId == 4) {
            var i = 6
            val value = buildString {
                do {
                    val groupType = input[i].digitToInt(2)
                    append(input.substring(i + 1, i + 5))
                    i += 5
                } while (groupType == 1)
            }.toLong(2)
            val packet = LiteralPacket(version, value)
            val rest = input.substring(i)
            ParseResult(packet, rest)
        } else {
            val constructor = getConstructor(packetTypeId)
            when (val lengthType = input[6].digitToInt(2)) {
                0 -> {
                    val subPacketsLength = input.substring(7, 22).toInt(2)
                    var remainingSubPackets = input.substring(22, 22 + subPacketsLength)
                    val subPackets = mutableListOf<Packet>()
                    while (remainingSubPackets.isNotEmpty()) {
                        val (packet, rest) = parse(remainingSubPackets)
                        subPackets.add(packet)
                        remainingSubPackets = rest
                    }
                    val packet = constructor(version, subPackets)
                    val rest = input.substring(22 + subPacketsLength)
                    ParseResult(packet, rest)
                }
                1 -> {
                    val numberOfSubPackets = input.substring(7, 18).toInt(2)
                    var rest = input.substring(18)
                    val subPackets = mutableListOf<Packet>()
                    repeat(numberOfSubPackets) {
                        val result = parse(rest)
                        subPackets.add(result.packet)
                        rest = result.rest
                    }
                    val packet = constructor(version, subPackets)
                    ParseResult(packet, rest)
                }
                else -> error("Unknown length type ID: $lengthType")
            }
        }
    }

    fun part1(input: Packet): Int {
        var sum = 0
        input.visit { sum += it.version }
        return sum
    }

    fun part2(input: Packet): Long = input.value

    fun readPuzzleInput (name: String) = parse(readInputAsString(name).toBigInteger(16).toString(2)).packet

    val testInput = readPuzzleInput("Day16_test")
    val input = readPuzzleInput("Day16")

//    check(part1(testInput) == 31)
    println(part1(input))

    check(part2(testInput) == 1L)
    println(part2(input))
}
