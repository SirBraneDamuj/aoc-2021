package com.zpthacker.aoc21.day5

import com.zpthacker.aoc21.getInputLines

fun main() {
    val lines = getInputLines(5)
    val lineSegments = lines.map {
        val (first, second) = it.split(" -> ")
        coordinateToPair(first) to coordinateToPair(second)
    }
    var maxX = 0
    var maxY = 0
    lineSegments.forEach { (first, second) ->
        val higherX = listOf(first.first, second.first).maxOrNull()!!
        val higherY = listOf(first.second, second.second).maxOrNull()!!
        if (higherX > maxX) {
            maxX = higherX
        }
        if (higherY > maxY) {
            maxY = higherY
        }
    }
    val grid = MutableList(maxY+1) {
        MutableList(maxX+1) { 0 }
    }

    val nonDiagonals = lineSegments
        .filter { (first, second) ->
            first.first == second.first ||
                first.second == second.second
        }
    lineSegments
        .forEach { (first, second) ->
            val (x1, y1) = first
            val (x2, y2) = second
            if (x1 == x2) {
                val (lowY, highY) = listOf(y1, y2).sorted()
                (lowY..highY).forEach { y ->
                    grid[y][x1]++
                }
            } else if (y1 == y2) {
                val (lowX, highX) = listOf(x1, x2).sorted()
                (lowX..highX).forEach { x ->
                    grid[y1][x]++
                }
            } else {
                var position = 0
                val (xs, ys) = if (y1 > y2) {
                    val xs = if (x1 > x2) {
                        (x1 downTo x2).toList()
                    } else {
                        (x1..x2).toList()
                    }
                    xs to (y1 downTo y2).toList()
                } else {
                    val xs = if (x1 > x2) {
                        (x1 downTo x2).toList()
                    } else {
                        (x1..x2).toList()
                    }
                    xs to (y1..y2).toList()
                }
                while (position < xs.count()) {
                    grid[ys[position]][xs[position]]++
                    position++
                }
            }
        }

    val answer = grid.fold(0) { sum, row ->
        sum + row.count { it > 1 }
    }
    println(answer)
}

fun coordinateToPair(coord: String): Pair<Int, Int> {
    return coord.split(",")
        .let { (x, y) -> x.toInt() to y.toInt() }
}