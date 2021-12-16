package com.zpthacker.aoc21.day16

import com.zpthacker.aoc21.binaryToDecimal
import com.zpthacker.aoc21.getInput

fun main() {
    var input = "A0016C880162017C3686B18A3D4780"
    input = getInput(16)
    val binary = input
        .trim()
        .map {
            when (it) {
                in '0'..'9' -> {
                    it.digitToInt()
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
    val packets = parsePacket(binary).first
    println(
        packets.sumOf {
            it.header.version
        }
    )
}
/*
00111000000000000110111101000101001010010001001000000000
VVVTTTILLLLLLLLLLLLLLLAAAAAAAAAAAaaaaaBBBBBBBBBBB
 */

fun parsePacket(p: String): Pair<List<Packet>, String> {
    var remainder = p
    val packets = mutableListOf<Packet>()
    while (remainder.length >= 11) {
        val (header, body) = parseHeader(remainder)
        if (header.version == 5) {
            println("stop")
        }
        val newPackets = when (header.typeId) {
            4 -> {
                val (packetBody, rest) = parseLiteral(body)
                remainder = rest
                listOf(
                    Packet(
                        header = header,
                        body = packetBody
                    )
                )
            }
            else -> {
                val (subpackets, rest) = parseOperator(body)
                remainder = rest
                listOf(Packet(header, body)) + subpackets
            }
        }
        packets.addAll(newPackets)
    }

    return packets to remainder
}

fun parseLiteral(p: String): Pair<String, String> {
    var literal = p
    var acc = ""
    var done = false
    while (!done) {
        val token = literal.take(5)
        literal = literal.drop(5)
        done = token.startsWith('0')
        acc += token.drop(1)
    }
    return acc to literal
}

fun parseOperator(p: String): Pair<List<Packet>, String> {
    return when (p.first()) {
        '0' -> {
            val lengthOfSubPackets = p.drop(1).take(15).binaryToDecimal()
            val subPacketsString = p.drop(16).take(lengthOfSubPackets)
            val remainder = p.drop(16).drop(lengthOfSubPackets)
            parsePacket(subPacketsString).first to remainder
        }
        '1' -> {
            val numberOfSubPackets = p.drop(1).take(11).binaryToDecimal()
            var remainder = p.drop(12)
            val subPackets = mutableListOf<Packet>()
            while (subPackets.count() < numberOfSubPackets) {
                val (newSubPackets, rest) = parsePacket(remainder)
                remainder = rest
                subPackets.addAll(newSubPackets)
            }
            subPackets to remainder
        }
        else -> throw RuntimeException()
    }
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
)

data class Header(
    val version: Int,
    val typeId: Int
)