package com.zpthacker.aoc21.day21

import com.zpthacker.aoc21.getInput

fun main() {
    var input = """
        Player 1 starting position: 4
        Player 2 starting position: 8
    """.trimIndent()
    input = getInput(21)
    val players = input
        .trim()
        .split("\n")
        .map {
            val position = it.last().digitToInt()
            val label = it[7].digitToInt()
            Player(label, 0, position-1)
        }
    var currentTurn = 0
    var currentRoll = 0
    val diceRange = (1..100).toList()
    while (players.all { it.score < 1000 }) {
        val currentPlayer = players[currentTurn % 2]
        val roll = (0 until 3).map {
            val roll = diceRange[currentRoll % diceRange.count()]
            currentRoll++
            roll
        }.sum()
        currentPlayer.move(roll)
        currentTurn++
    }
    val loser = players.minOf { it.score }
    println(loser * currentRoll)
}

private val positions = (1..10).toList()

data class Player(
    val label: Int,
    var score: Int,
    var position: Int,
) {
    fun move(x: Int) {
        position += x
        score += positions[position % positions.count()]
    }
}