package com.zpthacker.aoc21.day13

import com.zpthacker.aoc21.getInput

fun main() {
    val input = getInput(13)
    val (dotsString, foldsString) = input
        .trim()
        .split("\n")
        .filter(String::isNotBlank)
        .partition { !it.startsWith("fold") }
    val dots = dotsString
        .map {
            val (left, right) = it.split(",")
            left.toInt() to right.toInt()
        }
    val folds = foldsString
        .map {
            val (_, _, foldString) = it.split(" ")
            val (axis, num) = foldString.split("=")
            axis to num.toInt()
        }
    val activeDots = dots.toMutableSet()
    for (fold in folds) {
        val (axis, value) = fold
        when (axis) {
            "x" -> {
                val movingDots = activeDots.filter { (x, _) -> x >= value }.toSet()
                activeDots.removeAll(movingDots)
                movingDots.forEach { (x, y) ->
                    activeDots.add(
                        (x - ((x - value) * 2)) to y
                    )
                }
            }
            "y" -> {
                val movingDots = activeDots.filter { (_, y) -> y >= value }.toSet()
                activeDots.removeAll(movingDots)
                movingDots.forEach { (x, y) ->
                    activeDots.add(
                        x to (y - ((y - value) * 2))
                    )
                }
            }
            else -> throw RuntimeException()
        }
    }
    for (y in (0..activeDots.maxOf { (_, y) -> y })) {
        for (x in (0..activeDots.maxOf { (x, _) -> x })) {
            if ((x to y) in activeDots) {
                print("#")
            } else {
                print(" ")
            }
        }
        println()
    }
}













