package com.zpthacker.aoc21.day12

import com.zpthacker.aoc21.getInput

var connections = mutableMapOf<Cave, Set<Cave>>()
var caves = mutableMapOf<String, Cave>()

fun main() {
    val input = getInput(12).trim()
    val lines = input.split("\n")
    lines.fold(caves) { acc, caveLine ->
        val (first, second) = caveLine.split("-")
        if (first !in acc.keys) {
            val type = if (first.uppercase() == first) {
                CaveType.BIG
            } else {
                CaveType.SMALL
            }
            acc[first] = Cave(name = first, type = type)
        }
        if (second !in acc.keys) {
            val type = if (second.uppercase() == second) {
                CaveType.BIG
            } else {
                CaveType.SMALL
            }
            acc[second] = Cave(name = second, type = type)
        }
        acc
    }
    lines.fold(connections) { acc, line ->
        val (first, second) = line.split("-")
        val firstCave = caves[first]!!
        val secondCave = caves[second]!!
        if (acc[firstCave] == null) {
            acc[firstCave] = setOf(secondCave)
        } else {
            acc[firstCave] = acc[firstCave]!! + secondCave
        }
        if (acc[secondCave] == null) {
            acc[secondCave] = setOf(firstCave)
        } else {
            acc[secondCave] = acc[secondCave]!! + firstCave
        }
        acc
    }
    val start = caves["start"]!!
    val p = paths(start, mapOf(start to 2))
    val valid = p
        .filter { path ->
            path
                .split(",")
                .groupBy { it }
                .filter { (piece, pieces) ->
                    if (piece.uppercase() == piece) {
                        false
                    } else {
                        pieces.count() >= 2
                    }
                }
                .count() < 2
        }
    println(p.count())
}

fun paths(node: Cave, smallCavesVisited: Map<Cave, Int>): List<String> {
    return if (node.name == "end") {
        listOf("end")
    } else {
        val alreadyRevisitedCave = smallCavesVisited.any { (cave, times) ->
            cave.name != "start" && cave.name != "end" && times >= 2
        }
        if (alreadyRevisitedCave && node in smallCavesVisited.keys) {
            return listOf()
        }
        val neighbors = connections[node]!! - if (alreadyRevisitedCave) smallCavesVisited.keys else setOf(caves["start"]!!)
        if (neighbors.isEmpty()) {
            return listOf()
        }
        val newVisitList = if (node.type == CaveType.SMALL && node.name != "start" && node.name != "end") {
            smallCavesVisited + (node to (smallCavesVisited[node]?.inc() ?: 1))
        } else {
            smallCavesVisited
        }
        neighbors.flatMap { neighbor ->
            paths(
                node = neighbor,
                smallCavesVisited = newVisitList
            ).map { path ->
                "${node.name},$path"
            }
        }
    }
}

enum class CaveType {
    BIG,
    SMALL,
}

typealias CaveConnection = Pair<Cave, Cave>

data class Cave(
    val name: String,
    val type: CaveType
)
