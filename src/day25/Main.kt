package com.zpthacker.aoc21.day25

import com.zpthacker.aoc21.GridPosition
import com.zpthacker.aoc21.getInput

fun main() {
    val input = getInput(25)
    println("hello")
    val lines = input
        .trim()
        .split("\n")
    val height = lines.count()
    val width = lines.first().length
    val oceanFloor = OceanFloor(width, height)
    lines.forEachIndexed { y, line ->
        line.forEachIndexed { x, ch ->
            val cucumber = when (ch) {
                'v' -> SeaCucumber(Herd.SOUTH, y)
                '>' -> SeaCucumber(Herd.EAST, x)
                else -> null
            }
            if (cucumber != null) {
                oceanFloor.cucumbers[x to y] = cucumber
            }
        }
    }
    var stepsToTake = false
    var count = 0
    do {
        count++
        stepsToTake = oceanFloor.moveHerd(Herd.EAST)
        stepsToTake = oceanFloor.moveHerd(Herd.SOUTH) || stepsToTake
    } while (stepsToTake)
    println("Number of steps till halt: $count")
}

data class OceanFloor(
    val width: Int,
    val height: Int,
) {
    val cucumbers: MutableMap<GridPosition, SeaCucumber> = mutableMapOf()

    fun moveHerd(herd: Herd): Boolean {
        val moves = cucumbers
            .filter { (_, cucumber) -> cucumber.herd == herd }
            .mapNotNull { (position, cucumber) ->
                val move = cucumber.herd.nextPosition(position, this)
                if (move in cucumbers) {
                    null
                } else {
                    position to move
                }
            }
        moves.forEach { (start, end) ->
            val cucumber = this.cucumbers.remove(start)!!
            this.cucumbers[end] = cucumber
        }
        return moves.isNotEmpty()
    }
}

enum class Herd {
    EAST,
    SOUTH;

    fun nextPosition(gridPosition: GridPosition, oceanFloor: OceanFloor) =
        when (this) {
            EAST -> gridPosition.first.inc().mod(oceanFloor.width) to gridPosition.second
            SOUTH -> gridPosition.first to gridPosition.second.inc().mod(oceanFloor.height)
        }
}

data class SeaCucumber(
    val herd: Herd,
    val position: Int,
)