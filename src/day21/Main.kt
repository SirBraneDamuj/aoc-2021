package com.zpthacker.aoc21.day21

import com.zpthacker.aoc21.getInput

fun main() {
    val input = getInput(21)
    val originalPlayers = input
        .trim()
        .split("\n")
        .map {
            val position = it.last().digitToInt()
            val label = it[7].digitToInt()
            Player(label, 0, position - 1)
        }
    val players = listOf(originalPlayers[0].copy(), originalPlayers[1].copy())
    var currentTurn = 0
    var currentRoll = 0
    val diceRange = (1..100).toList()
    while (players.all { it.score < 1000 }) {
        val currentPlayer = players[currentTurn % 2]
        val roll = (0 until 3).sumOf {
            val roll = diceRange[currentRoll % diceRange.count()]
            currentRoll++
            roll
        }
        currentPlayer.move(roll)
        currentTurn++
    }
    val loser = players.minOf { it.score }
    println("Part 1: ${loser * currentRoll}")
    val diracPlayers = listOf(originalPlayers[0].copy(), originalPlayers[1].copy())
    val (player1Wins, player2Wins) = countWinners(diracPlayers, 0)
    println("Part 2: ${listOf(player1Wins, player2Wins).maxOrNull()}")
}

private val positions = (1..10).toList()

data class Player(
    val label: Int,
    var score: Int,
    var position: Int,
) {
    fun move(x: Int) {
        position += x
        if (position > 10) position -= 10
        score += positions[position % positions.count()]
    }
}

data class State(
    val player1: Player,
    val player2: Player,
    val turn: Int,
)

private val memo = mutableMapOf<State, Pair<Long, Long>>()

private val rolls = mutableListOf<Int>().apply {
    for (i in (1..3)) {
        for (j in (1..3)) {
            for (k in (1..3)) {
                this.add(i + j + k)
            }
        }
    }
}

fun countWinners(players: List<Player>, turn: Int): Pair<Long, Long> {
    val state = State(players[0], players[1], turn)
    val memoResult = memo[state]
    if (memoResult != null) {
        return memoResult
    }
    if (players[0].score >= 21) {
        return 1L to 0L
    } else if (players[1].score >= 21) {
        return 0L to 1L
    }
    val wins = rolls
        .map {
            val newPlayers = listOf(
                players[0].copy(),
                players[1].copy(),
            )
            newPlayers[turn].move(it)
            countWinners(newPlayers, if (turn == 0) 1 else 0)
        }
        .reduce { (player1Total, player2Total), (player1Wins, player2Wins) ->
            player1Total + player1Wins to player2Total + player2Wins
        }
    memo[state] = wins
    return wins
}
