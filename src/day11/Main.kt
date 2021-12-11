package com.zpthacker.aoc21.day11

import com.zpthacker.aoc21.Grid
import com.zpthacker.aoc21.GridPosition
import com.zpthacker.aoc21.getInput

fun main() {
    val input = getInput(11)
    val lines = input.split("\n")
    val height = lines.count()
    val width = lines.first().length
    val grid = Grid(height = height, width = width)
    lines.forEachIndexed { y, line ->
        line.forEachIndexed { x, ch ->
            grid.addPoint(
                grid.Point(x = x, y = y, value = ch.digitToInt())
            )
        }
    }
    var flashCount = 0
    var firstStep: Int? = null
    var step = 1
    while (step < 100 || firstStep == null) {
        grid.allPoints.forEach { point -> point.value++ }
        val flashPoints = grid.allPoints.filter { it.value > 9 }
        if (flashPoints.isNotEmpty()) {
            val flashed = mutableSetOf<GridPosition>()
            val toVisit = ArrayDeque<Grid.Point>()
            toVisit.addAll(flashPoints)
            flashed.addAll(flashPoints.map(Grid.Point::position))
            do {
                val current = toVisit.removeFirst()
                current.value = 0
                for (newNeighbor in current.allNeighbors) {
                    if (newNeighbor.position in flashed) continue
                    newNeighbor.value++
                    if (newNeighbor.value > 9) {
                        toVisit.add(newNeighbor)
                        flashed.add(newNeighbor.position)
                    }
                }
            } while (toVisit.isNotEmpty())
            if (firstStep == null && flashed.count() == grid.allPoints.count()) {
                firstStep = step
            }
            if (step <= 100) flashCount += flashed.count()
        }
        step++
    }
    println("Day 11: Dumbo Octopus")
    println("Part 1: $flashCount")
    println("Part 2: $firstStep")
}