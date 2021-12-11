package com.zpthacker.aoc21.day11

import com.zpthacker.aoc21.day9.Grid
import com.zpthacker.aoc21.day9.Point
import com.zpthacker.aoc21.getInput

fun main() {
    val input = getInput(11)
//    val input = """
//        5483143223
//        2745854711
//        5264556173
//        6141336146
//        6357385478
//        4167524645
//        2176841721
//        6882881134
//        4846848554
//        5283751526
//    """.trimIndent()
    val lines = input.split("\n")
    val height = lines.count()
    val width = lines.first().length
    val grid = Grid(height = height, width = width)
    lines.forEachIndexed { y, line ->
        line.forEachIndexed { x, ch ->
            grid.addPoint(
                Point(x = x, y = y, depth = ch.digitToInt())
            )
        }
    }
    var flashCount = 0
    var firstStep = -1
    repeat(1000) {
        grid.allPoints.forEach {
            it.depth++
        }
        val flashPoints = grid.allPoints.filter { it.depth > 9 }
        if (flashPoints.isEmpty()) return@repeat
        val flashed = mutableSetOf<Pair<Int, Int>>()
        val toVisit = ArrayDeque<Point>()
        toVisit.addAll(flashPoints)
        flashed.addAll(flashPoints.map(Point::position))
        do {
            val current = toVisit.removeFirst()
            current.depth = 0
            for (newNeighbor in current.neighbors(grid)) {
                if (newNeighbor.position in flashed) continue
                newNeighbor.depth++
                if (newNeighbor.depth > 9) {
                    toVisit.add(newNeighbor)
                    flashed.add(newNeighbor.position)
                }
            }
        } while (toVisit.isNotEmpty())
        if (flashed.count() == grid.allPoints.count() && firstStep == -1) {
            firstStep = it
        }
        flashCount += flashed.count()
    }
    println(flashCount)
    println(firstStep + 1)
}