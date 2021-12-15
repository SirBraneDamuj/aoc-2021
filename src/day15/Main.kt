package com.zpthacker.aoc21.day15

import com.zpthacker.aoc21.Grid
import com.zpthacker.aoc21.getInput
import java.util.*
import kotlin.math.min

fun main() {
    val input = getInput(15)

    val lines = input.trim().split("\n")
    val width = lines.first().length
    val height = lines.count()

    val grid = Grid(width = width * 5, height = height * 5)
    for (y in (0 until (height * 5))) {
        for (x in (0 until (width * 5))) {
            val value = (lines[y % height][x % width].digitToInt() + (x / width) + (y / height))
            grid.addPoint(
                grid.Point(
                    x = x,
                    y = y,
                    value = if (value == 9) 9 else value % 9
                )
            )
        }
    }
    val root = grid.getPoint(0 to 0)
    val distances = mutableMapOf(root to 0)
    val visited = mutableSetOf<Grid.Point>()
    val queue = PriorityQueue<Path> { a, b ->
        a.distance.compareTo(b.distance)
    }
    queue.add(Path(0, 0, 0))
    visited.add(root)
    while (!queue.isEmpty() && !queue.peek().let { it.x == (width * 5) - 1 && it.y == (height * 5) - 1 }) {
        val next = queue.poll()
        val current = grid.getPoint(next.x to next.y)
        val currentDistance = next.distance
        val neighbors = current.cardinalNeighbors
        for (neighbor in neighbors) {
            if (neighbor in visited) continue
            val distance = min(distances[neighbor] ?: Int.MAX_VALUE, currentDistance + neighbor.value)
            distances[neighbor] = distance
            queue.add(Path(neighbor.x, neighbor.y, distance))
            visited.add(neighbor)
        }
    }
    println(queue.peek().distance)
}

data class Path(
    val x: Int,
    val y: Int,
    val distance: Int
)