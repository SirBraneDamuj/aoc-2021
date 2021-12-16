package com.zpthacker.aoc21.day16

import com.zpthacker.aoc21.binaryToDecimal
import com.zpthacker.aoc21.binaryToDecimalLong
import com.zpthacker.aoc21.getInput
import com.zpthacker.aoc21.takeAndRest

fun main() {
    val binary = getInput(16)
        .trim()
        .uppercase()
        .map { ch ->
            when (ch) {
                in '0'..'9' -> {
                    ch.digitToInt()
                }
                'A' -> 10
                'B' -> 11
                'C' -> 12
                'D' -> 13
                'E' -> 14
                'F' -> 15
                else -> throw RuntimeException()
            }.let(Integer::toBinaryString).padStart(4, '0')
        }
        .joinToString("")
    val (packets, remainder) = parsePacket(binary, 0)
    val versionSum = packets.sumOf { it.header.version }
    val total = packets.single { it.depth == 0 }.value
    println("Part 1: $versionSum")
    println("Part 2: $total")
    println("And there was some garbage at the end... $remainder")
}

fun parsePacket(p: String, depth: Int): Pair<List<Packet>, String> {
    val remainder: String
    val (header, body) = parseHeader(p)
    val packets = when (header.typeId) {
        4 -> {
            val (packetBody, rest) = parseLiteral(body)
            remainder = rest
            listOf(
                Packet(
                    header = header,
                    body = packetBody,
                    value = packetBody.binaryToDecimalLong(),
                    depth = depth,
                )
            )
        }
        else -> {
            val (subPackets, rest) = parseOperator(body, depth)
            val operands = subPackets.filter { it.depth == depth+1 }
            remainder = rest
            val value = when (header.typeId) {
                0 -> operands.sumOf { it.value }
                1 -> operands.fold(1L) { acc, packet -> packet.value * acc }
                2 -> operands.minOf { it.value }
                3 -> operands.maxOf { it.value }
                5 -> {
                    val (left, right) = operands
                    if (left.value > right.value) 1 else 0
                }
                6 -> {
                    val (left, right) = operands
                    if (left.value < right.value) 1 else 0
                }
                7 -> {
                    val (left, right) = operands
                    if (left.value == right.value) 1 else 0
                }
                else -> throw RuntimeException()
            }
            listOf(Packet(header, body.dropLast(remainder.length), value, depth)) + subPackets
        }
    }

    return packets to remainder
}

fun parseLiteral(p: String): Pair<String, String> {
    var remainingPacket = p
    var acc = ""
    var done = false
    while (!done) {
        val (token, remainder) = remainingPacket.takeAndRest(5)
        remainingPacket = remainder
        val (signal, value) = token.takeAndRest(1)
        done = signal.startsWith('0')
        acc += value
    }
    return acc to remainingPacket
}

fun parseOperator(p: String, depth: Int): Pair<List<Packet>, String> {
    val (lengthType, rest) = p.takeAndRest(1)
    return when (lengthType) {
        "0" -> parseLengthwiseSubPackets(rest, depth)
        "1" -> parseCountedSubPackets(rest, depth)
        else -> throw RuntimeException()
    }
}

fun parseLengthwiseSubPackets(p: String, depth: Int): Pair<List<Packet>, String> {
    val (lengthOfSubPackets, rest) = p.takeAndRest(15)
    val (subPacketsString, remainder) = rest.takeAndRest(lengthOfSubPackets.binaryToDecimal())
    val subPackets = mutableListOf<Packet>()
    var remainingSubPacketsString = subPacketsString
    while (remainingSubPacketsString.isNotEmpty()) {
        val (newSubPackets, remainingSubPackets) = parsePacket(remainingSubPacketsString, depth+1)
        remainingSubPacketsString = remainingSubPackets
        subPackets.addAll(newSubPackets)
    }
    return subPackets to remainder
}

fun parseCountedSubPackets(p: String, depth: Int): Pair<List<Packet>, String> {
    val (numberOfSubPacketsBinary, remainingData) = p.takeAndRest(11)
    val numberOfSubPackets = numberOfSubPacketsBinary.binaryToDecimal()
    var remainder = remainingData
    val subPackets = mutableListOf<Packet>()
    while (subPackets.count { it.depth == depth+1 } < numberOfSubPackets) {
        val (newSubPackets, rest) = parsePacket(remainder, depth+1)
        remainder = rest
        subPackets.addAll(newSubPackets)
    }
    return subPackets to remainder
}

fun parseHeader(p: String): Pair<Header, String> {
    val version = p.take(3).binaryToDecimal()
    val typeId = p.drop(3).take(3).binaryToDecimal()
    val rest = p.drop(6)
    return Header(version, typeId) to rest
}

data class Packet(
    val header: Header,
    val body: String,
    val value: Long,
    val depth: Int,
)

data class Header(
    val version: Int,
    val typeId: Int
) {
    fun toBinary() =
        Integer.toBinaryString(version).padStart(3, '0') + Integer.toBinaryString(typeId).padStart(3, '0')
}