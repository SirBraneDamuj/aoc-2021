package com.zpthacker.aoc21.day9

import com.zpthacker.aoc21.getInput

class Grid(
    val height: Int,
    val width: Int,
) {
    private val _grid: MutableMap<Pair<Int, Int>, Point> = mutableMapOf()

    val allPoints: List<Point>
        get() = _grid.values.toList()

    fun getPoint(position: Pair<Int, Int>) =
        _grid[position].let {
            if (it == null) {
                println("oh dear")
                throw RuntimeException()
            } else {
                it
            }
        }

    fun addPoint(point: Point) {
        _grid[point.position] = point
    }
}

data class Point(
    val x: Int,
    val y: Int,
    val depth: Int,
) {
    val position: Pair<Int, Int> = (x to y)

    fun neighbors(grid: Grid): List<Point> {
        val topLine = y == 0
        val bottomLine = y == grid.height - 1
        val leftMost = x == 0
        val rightMost = x == grid.width - 1
        val neighborPositions = when {
            topLine && leftMost -> {
                listOf(
                    x.inc() to y,
                    x to y.inc(),
                )
            }
            topLine && rightMost -> {
                listOf(
                    x.dec() to y,
                    x to y.inc(),
                )
            }
            topLine -> {
                listOf(
                    x.dec() to y,
                    x.inc() to y,
                    x to y.inc(),
                )
            }
            bottomLine && leftMost -> {
                listOf(
                    x.inc() to y,
                    x to y.dec(),
                )
            }
            bottomLine && rightMost -> {
                listOf(
                    x.dec() to y,
                    x to y.dec(),
                )
            }
            bottomLine -> {
                listOf(
                    x.dec() to y,
                    x.inc() to y,
                    x to y.dec(),
                )
            }
            leftMost -> {
                listOf(
                    x.inc() to y,
                    x to y.dec(),
                    x to y.inc(),
                )
            }
            rightMost -> {
                listOf(
                    x.dec() to y,
                    x to y.dec(),
                    x to y.inc(),
                )
            }
            else -> {
                listOf(
                    x.dec() to y,
                    x.inc() to y,
                    x to y.dec(),
                    x to y.inc(),
                )
            }
        }
        return neighborPositions.map(grid::getPoint)
    }
}

fun main() {
    val input = getInput(9)
//    val input = """
//        2199943210
//        3987894921
//        9856789892
//        8767896789
//        9899965678
//    """.trimIndent()
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
                Point(
                    depth = depth,
                    x = x,
                    y = y,
                )
            )
        }
    }

    val lowPoints = grid.allPoints.filter { point ->
        point.neighbors(grid).all { neighbor ->
            neighbor.depth > point.depth
        }
    }
    val part1 = lowPoints.sumOf { it.depth + 1 }
    println(part1)
    val basins = lowPoints.map { lowPoint ->
        val visited = mutableSetOf<Pair<Int, Int>>()
        val toVisit = ArrayDeque<Point>()
        toVisit.add(lowPoint)
        do {
            val current = toVisit.removeFirst()
            if (current.depth == 9) continue
            visited.add(current.position)
            for (newNeighbor in current.neighbors(grid)) {
                if (newNeighbor.depth > current.depth && newNeighbor.depth != 9 && newNeighbor.position !in visited) {
                    toVisit.add(newNeighbor)
                }
            }
        } while (toVisit.isNotEmpty())
        visited
    }
    val answer = basins.map { it.count() }.sortedDescending().take(3).reduce(Int::times)
    println(answer)
}
