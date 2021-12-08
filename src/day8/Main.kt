package com.zpthacker.aoc21.day8

import com.zpthacker.aoc21.getInput

fun main() {
    val input = getInput(8)
    println(input)
    val lines = input.split("\n")
    val answer = lines.sumOf { line ->
        val split = line.split("|")
        val signals = split[0].trim().split(" ")
        val outputs = split[1].trim().split(" ")
        val eightMapping = signals.single { it.length == 7 }.toSet()
        val sevenMapping = signals.single { it.length == 3 }.toSet()
        val fourMapping = signals.single { it.length == 4 }.toSet()
        val oneMapping = signals.single { it.length == 2 }.toSet()
        val rightSide = eightMapping.intersect(oneMapping)
        val top = (eightMapping.intersect(sevenMapping) - rightSide).single()
        val bottomAndBottomLeft = (eightMapping - fourMapping - top)
        val topLeftAndMiddle = fourMapping - rightSide
        val zeroMapping = signals.map(String::toSet).single {
            val difference = eightMapping - it
            difference.count() == 1 && topLeftAndMiddle.containsAll(difference)
        }
        val middle = (eightMapping - zeroMapping).single()
        val topLeft = (topLeftAndMiddle - middle).single()
        val threeMapping = signals.map(String::toSet).single {
            val difference = eightMapping - it
            difference.count() == 2 && difference.contains(topLeft) && difference.intersect(rightSide).isEmpty()
        }
        val sixMapping = signals.map(String::toSet).single {
            val difference = eightMapping - it
            difference.count() == 1 && rightSide.containsAll(difference)
        }
        val nineMapping = signals.map(String::toSet).single {
            val difference = eightMapping - it
            difference.count() == 1 && !rightSide.containsAll(difference) && !difference.contains(middle)
        }
        val topRight = (eightMapping - sixMapping).single()
        val bottom = (threeMapping - top - rightSide - middle).single()
        val bottomLeft = (bottomAndBottomLeft - bottom).single()
        val twoMapping = setOf(top, topRight, middle, bottomLeft, bottom)
        val bottomRight = (rightSide - topRight).single()
        val fiveMapping = setOf(top, topLeft, middle, bottomRight, bottom)
        val mappings = mapOf(
            zeroMapping to 0,
            oneMapping to 1,
            twoMapping to 2,
            threeMapping to 3,
            fourMapping to 4,
            fiveMapping to 5,
            sixMapping to 6,
            sevenMapping to 7,
            eightMapping to 8,
            nineMapping to 9
        )
        val number = outputs.fold("") { n: String, digit: String ->
            val map = mappings[digit.toSet()]
            if (map == null) {
                println("oh dear")
            }
            n + mappings[digit.toSet()]
        }
        number.toInt()
    }
    println(answer)
}