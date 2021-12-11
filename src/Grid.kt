package com.zpthacker.aoc21

typealias GridPosition = Pair<Int, Int>

class Grid(
    val height: Int,
    val width: Int,
) {
    private val _grid: MutableMap<GridPosition, Point> = mutableMapOf()

    val allPoints: List<Point>
        get() = _grid.values.toList()

    fun getPoint(position: GridPosition) =
        _grid[position].let {
            if (it == null) {
                println("oh dear")
                throw RuntimeException()
            } else {
                it
            }
        }

    fun tryPoint(position: GridPosition) =
        _grid[position]

    fun addPoint(point: Point) {
        _grid[point.position] = point
    }

    fun dump() {
        (0 until height).forEach { y ->
            (0 until width).forEach { x ->
                print(getPoint(x to y).value)
            }
            println()
        }
    }

    inner class Point(
        val x: Int,
        val y: Int,
        var value: Int,
    ) {
        val position: GridPosition = (x to y)
        private val topLine = y == 0
        private val bottomLine = y == height - 1
        private val leftMost = x == 0
        private val rightMost = x == width - 1

        private val cardinalNeighborPositions: Set<GridPosition> =
            when {
                topLine && leftMost -> {
                    setOf(
                        x.inc() to y,
                        x to y.inc(),
                    )
                }
                topLine && rightMost -> {
                    setOf(
                        x.dec() to y,
                        x to y.inc(),
                    )
                }
                topLine -> {
                    setOf(
                        x.dec() to y,
                        x.inc() to y,
                        x to y.inc(),
                    )
                }
                bottomLine && leftMost -> {
                    setOf(
                        x.inc() to y,
                        x to y.dec(),
                    )
                }
                bottomLine && rightMost -> {
                    setOf(
                        x.dec() to y,
                        x to y.dec(),
                    )
                }
                bottomLine -> {
                    setOf(
                        x.dec() to y,
                        x.inc() to y,
                        x to y.dec(),
                    )
                }
                leftMost -> {
                    setOf(
                        x.inc() to y,
                        x to y.dec(),
                        x to y.inc(),
                    )
                }
                rightMost -> {
                    setOf(
                        x.dec() to y,
                        x to y.dec(),
                        x to y.inc(),
                    )
                }
                else -> {
                    setOf(
                        x.dec() to y,
                        x.inc() to y,
                        x to y.dec(),
                        x to y.inc(),
                    )
                }
        }

        private val diagonalNeighborPositions: Set<GridPosition> =
            when {
                topLine && leftMost -> {
                    setOf(
                        x.inc() to y.inc(),
                    )
                }
                topLine && rightMost -> {
                    setOf(
                        x.dec() to y.inc(),
                    )
                }
                topLine -> {
                    setOf(
                        x.dec() to y.inc(),
                        x.inc() to y.inc(),
                    )
                }
                bottomLine && leftMost -> {
                    setOf(
                        x.inc() to y.dec(),
                    )
                }
                bottomLine && rightMost -> {
                    setOf(
                        x.dec() to y.dec(),
                    )
                }
                bottomLine -> {
                    setOf(
                        x.dec() to y.dec(),
                        x.inc() to y.dec(),
                    )
                }
                leftMost -> {
                    setOf(
                        x.inc() to y.dec(),
                        x.inc() to y.inc(),
                    )
                }
                rightMost -> {
                    setOf(
                        x.dec() to y.dec(),
                        x.dec() to y.inc(),
                    )
                }
                else -> {
                    setOf(
                        x.dec() to y.dec(),
                        x.dec() to y.inc(),
                        x.inc() to y.dec(),
                        x.inc() to y.inc(),
                    )
                }
            }

        val cardinalNeighbors: List<Point>
            get() = cardinalNeighborPositions.map(::getPoint)
        val diagonalNeighbors: List<Point>
            get() = diagonalNeighborPositions.map(::getPoint)
        val allNeighbors: List<Point>
            get() = (diagonalNeighborPositions + cardinalNeighborPositions).map(::getPoint)
    }
}
