package com.zpthacker.aoc21.day12

import com.zpthacker.aoc21.getInput

private var connections = mutableMapOf<Cave, Set<Cave>>()
private var caves = mutableMapOf<String, Cave>()

fun main() {
    val input = getInput(12).trim()
    val lines = input.split("\n")
    lines.fold(caves) { acc, caveLine ->
        caveLine
            .split("-")
            .forEach { caveName ->
                acc[caveName] = Cave(name = caveName)
            }
        acc
    }
    lines.fold(connections) { acc, line ->
        val (left, right) = line.split("-")
        listOf(
            left to right,
            right to left,
        ).forEach { (from, to) ->
            if (to == "start") {
                return@forEach
            } else {
                val fromCave = caves[from]!!
                val toCave = caves[to]!!
                if (acc[fromCave] == null) {
                    acc[fromCave] = setOf(toCave)
                } else {
                    acc[fromCave] = acc[fromCave]!! + toCave
                }
            }
        }
        acc
    }
    val start = caves["start"]!!
    val part1 = paths(
        node = start,
        maxRevisits = 0,
    )
    val part2 = paths(
        node = start,
        maxRevisits = 1,
    )
    println("Day 12: Passage Pathing")
    println("Part 1: ${part1.count()}")
    println("Part 2: ${part2.count()}")
}

fun paths(
    node: Cave,
    maxRevisits: Int,
    smallCavesVisited: Map<Cave, Int> = mapOf(),
): List<String> {
    return when (node.name) {
        "end" -> {
            listOf("end")
        }
        else -> {
            val revisitLimitReached = smallCavesVisited.any { (_, times) -> times > maxRevisits }
            if (revisitLimitReached && node in smallCavesVisited.keys) {
                return listOf()
            }
            val newVisitList = if (node.type == CaveType.SMALL) {
                smallCavesVisited + (node to (smallCavesVisited[node]?.inc() ?: 1))
            } else {
                smallCavesVisited
            }
            connections[node]!!.flatMap { neighbor ->
                paths(
                    node = neighbor,
                    smallCavesVisited = newVisitList,
                    maxRevisits = maxRevisits
                ).map { path ->
                    "${node.name},$path"
                }
            }
        }
    }
}

enum class CaveType {
    BIG,
    SMALL,
}

data class Cave(
    val name: String,
) {
    val type = if (name.uppercase() == name) {
        CaveType.BIG
    } else {
        CaveType.SMALL
    }
}
