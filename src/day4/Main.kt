package com.zpthacker.aoc21.day4

import com.zpthacker.aoc21.getInputLines
import java.util.regex.Pattern

fun main() {
    val lines = getInputLines(4)
//    val lines = """
//7,4,9,5,11,17,23,2,0,14,21,24,10,16,13,6,15,25,12,22,18,20,8,19,3,26,1
//
//22 13 17 11  0
// 8  2 23  4 24
//21  9 14 16  7
// 6 10  3 18  5
// 1 12 20 15 19
//
// 3 15  0  2 22
// 9 18 13 17  5
//19  8  7 25 23
//20 11 10 24  4
//14 21 16 12  6
//
//14 21 17 24  4
//10 16 15  9 19
//18  8 23 26 20
//22 11 13  6  5
// 2  0 12  3  7
//    """.trimIndent().split("\n")
    val drawn = lines.first().split(",").map(String::toInt)
    val boards = lines
        .drop(1)
        .filter { it.isNotBlank() }
        .flatMap { it.split(" ").filter(String::isNotBlank).map(String::toInt) }
        .chunked(5)
        .chunked(5)
        .map {
            Board(it)
        }
    val lastCalled = drawn.find { marked ->
        boards.forEach { board ->
            board.mark(marked)
        }
        boards.any { it.score != -1 }
    }!!
    val winnerBoard = boards.find { it.score != -1 }!!
    val winnerScore = winnerBoard.score
    println(winnerScore * lastCalled)
    var loserBoards = lines
        .drop(1)
        .filter { it.isNotBlank() }
        .flatMap { it.split(" ").filter(String::isNotBlank).map(String::toInt) }
        .chunked(5)
        .chunked(5)
        .map {
            Board(it)
        }
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
    val loserScore = loserBoard.score
    println(loserBoard.score * loserDraw)
}

class Board(val numbers: List<List<Int>>) {
    var rowMarks = MutableList(numbers.count()) { 0 }
    var colMarks = MutableList(numbers.count()) { 0 }
    var marked = mutableSetOf<Int>()

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
        numbers.forEachIndexed { rowIndex, row  ->
            row.forEachIndexed { colIndex, cell ->
                if (cell == number) {
                    rowMarks[rowIndex]++
                    colMarks[colIndex]++
                }
            }
        }
    }

}