package com.zpthacker.aoc21.day4

import com.zpthacker.aoc21.getInputLines

fun main() {
    val lines = getInputLines(4)
    val drawn = lines.first().split(",").map(String::toInt)
    val boardLines = lines
        .drop(1)
        .flatMap { it.split(" ").filter(String::isNotBlank).map(String::toInt) }
        .chunked(5)
        .chunked(5)
    val winnerBoards = boardLines.map(::Board)
    val lastCalled = drawn.find { marked ->
        winnerBoards.forEach { board ->
            board.mark(marked)
        }
        winnerBoards.any { it.score != -1 }
    }!!
    val winnerBoard = winnerBoards.find { it.score != -1 }!!
    val winnerScore = winnerBoard.score
    println(winnerScore * lastCalled)
    var loserBoards = boardLines.map(::Board)
    val loserDraw = drawn.find { marked ->
        loserBoards.forEach { board ->
            board.mark(marked)
        }
        if (loserBoards.count() > 1) {
            loserBoards = loserBoards.filter { it.score == -1 }
            false
        } else {
            loserBoards.single().score != -1
        }
    }!!
    val loserBoard = loserBoards.single()
    println(loserBoard.score * loserDraw)
}

class Board(private val numbers: List<List<Int>>) {
    private var rowMarks = MutableList(numbers.count()) { 0 }
    private var colMarks = MutableList(numbers.count()) { 0 }
    private var marked = mutableSetOf<Int>()

    val score: Int
        get() {
            val rowWinner = rowMarks.indexOfFirst { it == numbers.count() }
            val colWinner = colMarks.indexOfFirst { it == numbers.count() }
            return if (rowWinner != -1 || colWinner != -1) {
                numbers.flatMap { row ->
                    row.filter { it !in marked }
                }.sum()
            } else {
                -1
            }
        }

    fun mark(number: Int) {
        if (number in marked) return
        marked.add(number)
        numbers.forEachIndexed { rowIndex, row ->
            row.forEachIndexed { colIndex, cell ->
                if (cell == number) {
                    rowMarks[rowIndex]++
                    colMarks[colIndex]++
                }
            }
        }
    }
}