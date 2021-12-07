package com.zpthacker.aoc21.day7

import com.zpthacker.aoc21.getInputLines
import kotlin.math.abs

fun main() {
    val lines = getInputLines(7)
    val crabs = lines.first().split(",").map(String::toInt)
    val minPosition = crabs.minOrNull()!!
    val maxPosition = crabs.maxOrNull()!!

    val answer = (minPosition..maxPosition).minOf { position ->
        crabs.fold(0) { fuel, crab ->
            val distance = abs(crab - position)
            var delta = (1..distance).sum()
            fuel + delta
        }
    }
    println(answer)
}