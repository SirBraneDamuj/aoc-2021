package com.zpthacker.aoc21.day9

import com.zpthacker.aoc21.Grid
import com.zpthacker.aoc21.getInput

fun main() {
    val input = getInput(9)
    println(input)
    val lines = input.split("\n")
    val grid = Grid(
        height = lines.count(),
        width = lines.first().length
    )
    lines.forEachIndexed { y, line ->
        line.forEachIndexed { x, ch ->
            val depth = ch.digitToInt()
            grid.addPoint(
                grid.Point(
                    value = depth,
                    x = x,
                    y = y,
                )
            )
        }
    }

    val lowPoints = grid.allPoints.filter { point ->
        point.cardinalNeighbors.all { neighbor ->
            neighbor.value > point.value
        }
    }
    val part1 = lowPoints.sumOf { it.value + 1 }
    println(part1)
    val basins = lowPoints.map { lowPoint ->
        val visited = mutableSetOf<Pair<Int, Int>>()
        val toVisit = ArrayDeque<Grid.Point>()
        toVisit.add(lowPoint)
        do {
            val current = toVisit.removeFirst()
            if (current.value == 9) continue
            visited.add(current.position)
            for (newNeighbor in current.cardinalNeighbors) {
                if (newNeighbor.value > current.value && newNeighbor.value != 9 && newNeighbor.position !in visited) {
                    toVisit.add(newNeighbor)
                }
            }
        } while (toVisit.isNotEmpty())
        visited
    }
    val answer = basins.map { it.count() }.sortedDescending().take(3).reduce(Int::times)
    println(answer)
}
