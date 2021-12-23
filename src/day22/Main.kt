package com.zpthacker.aoc21.day22

import com.zpthacker.aoc21.getInput

fun main() {
    val input = getInput(22)

    var on = listOf<Box>()
    input
        .trim()
        .split("\n")
        .forEach { line ->
            val (state, coordinates) = line.split(" ")
            val (xRange, yRange, zRange) = coordinates
                .split(",")
                .map { range ->
                    val (_, extents) = range.split("=")
                    val (lower, upper) = extents.split("..").map(String::toInt)
                    lower to upper
                }
            val current = Box(
                x1 = xRange.first,
                x2 = xRange.second + 1,
                y1 = yRange.first,
                y2 = yRange.second + 1,
                z1 = zRange.first,
                z2 = zRange.second + 1,
            )
            on = on.flatMap { it.splitWith(current) } + if (state == "on") listOf(current) else listOf()
        }
    val count = on.sumOf { it.count }
    println(count)
}

data class Box(
    val x1: Int,
    val x2: Int,
    val y1: Int,
    val y2: Int,
    val z1: Int,
    val z2: Int,
) {
    fun contains(other: Box) =
        x1 <= other.x1 &&
            x2 >= other.x2 &&
            y1 <= other.y1 &&
            y2 >= other.y2 &&
            z1 <= other.z1 &&
            z2 >= other.z2

    fun intersects(other: Box) =
        x1 <= other.x2 &&
            x2 >= other.x1 &&
            y1 <= other.y2 &&
            y2 >= other.y1 &&
            z1 <= other.z2 &&
            z2 >= other.z1

    fun splitWith(other: Box): List<Box> {
        if (other.contains(this)) return listOf()
        if (!intersects(other)) return listOf(this)

        val xSplits = listOf(other.x1, other.x2).filter { this.x1 < it && it < this.x2 }
        val ySplits = listOf(other.y1, other.y2).filter { this.y1 < it && it < this.y2 }
        val zSplits = listOf(other.z1, other.z2).filter { this.z1 < it && it < this.z2 }

        val xBounds = listOf(x1) + xSplits + listOf(x2)
        val yBounds = listOf(y1) + ySplits + listOf(y2)
        val zBounds = listOf(z1) + zSplits + listOf(z2)

        val sections = mutableListOf<Box>()
        for (i in (0 until xBounds.count() - 1)) {
            for (j in (0 until yBounds.count() - 1)) {
                for (k in (0 until zBounds.count() - 1)) {
                    sections.add(
                        Box(
                            x1 = xBounds[i],
                            x2 = xBounds[i + 1],
                            y1 = yBounds[j],
                            y2 = yBounds[j + 1],
                            z1 = zBounds[k],
                            z2 = zBounds[k + 1],
                        )
                    )
                }
            }
        }
        return sections.filter { !other.contains(it) }
    }

    val count = (x2 - x1).toLong() * (y2 - y1).toLong() * (z2 - z1).toLong()
}
