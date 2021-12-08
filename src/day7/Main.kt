package com.zpthacker.aoc21.day7

import com.zpthacker.aoc21.getInputLines
import kotlin.math.abs

fun main() {
    val lines = getInputLines(7)
    val crabs = lines.first().split(",").map(String::toInt)
    val minPosition = crabs.minOrNull()!!
    val maxPosition = crabs.maxOrNull()!!

    val part1Answer = (minPosition..maxPosition).minOf { positionCandidate ->
        linearFuelUsage(crabs, positionCandidate)
    }
    val part2Answer = (minPosition..maxPosition).minOf { positionCandidate ->
        rampingFuelUsage(crabs, positionCandidate)
    }
    println("Day 7: The Treachery of Whales")
    println("Part 1 Answer: $part1Answer")
    println("Part 2 Answer: $part2Answer")
}

fun linearFuelUsage(crabs: List<Int>, destination: Int) =
    crabs.sumOf { crab ->
        abs(crab - destination)
    }

fun rampingFuelUsage(crabs: List<Int>, destination: Int) =
    crabs.sumOf { crab ->
        val distance = abs(crab - destination)
        (distance * (distance + 1)) / 2
    }
