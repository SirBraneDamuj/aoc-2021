package com.zpthacker.aoc21.day17

import com.zpthacker.aoc21.getInput
import com.zpthacker.aoc21.takeAndRest
import java.lang.Integer.max

fun main() {
    val input = getInput(17)
    println("hello")
    val (xTargetRange, yTargetRange) = input
        .trim()
        .drop(13)
        .split(", ")
        .map {
            val (leftExtent, rightExtent) = it.split("=")[1].split("..").map(String::toInt)
            leftExtent..rightExtent

        }
    val maxXVelocity = xTargetRange.last
    val minYVelocity = yTargetRange.first
    var totalMaxY = Int.MIN_VALUE
    val allVelocities = mutableListOf<Pair<Int, Int>>()
    for (xVelocity in (1..maxXVelocity)) {
        for (yVelocity in (minYVelocity..1000)) {
            val (_, maxY) = tryVelocity(xVelocity, yVelocity, xTargetRange, yTargetRange) ?: continue
            allVelocities.add(xVelocity to yVelocity)
            totalMaxY = max(totalMaxY, maxY)
        }
    }
    println("Part 1: $totalMaxY")
    println("Part 2: ${allVelocities.count()}")
}

fun tryVelocity(xVelocity: Int, yVelocity: Int, xRange: IntRange, yRange: IntRange): Pair<Int, Int>? {
    var x = 0
    var y = 0
    var maxX = x
    var maxY = y
    var currentXVelocity = xVelocity
    var currentYVelocity = yVelocity
    while (true) {
        if (currentXVelocity == 0 && y < yRange.first) {
            return null
        } else if (x in xRange && y in yRange) {
            return maxX to maxY
        } else {
            x += currentXVelocity
            maxX = max(x, maxX)
            y += currentYVelocity
            maxY = max(y, maxY)
            currentXVelocity = when {
                currentXVelocity < 0 -> currentXVelocity + 1
                currentXVelocity > 0 -> currentXVelocity - 1
                else -> currentXVelocity // should be zero
            }
            currentYVelocity--
        }
    }
}