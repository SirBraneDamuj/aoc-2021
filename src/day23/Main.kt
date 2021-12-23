package com.zpthacker.aoc21.day23

import com.zpthacker.aoc21.getInput
import java.util.*

fun main() {
    val input = getInput(23)
    val lines = input.trim().split("\n")
    val lines2 = lines.take(3) +
        listOf(
            "  #D#C#B#A#",
            "  #D#B#A#C#",
        ) +
        lines.takeLast(2)
    val initialEvaluator = AmphipodEvaluator(lines, 2)
    println("Part 1:")
    println(initialEvaluator.dump())
    val part1StartTime = System.currentTimeMillis()
    val (_, part1Score) = initialEvaluator.findLowestEnergy()
    val part1EndTime = System.currentTimeMillis()
    println("Minimum energy: $part1Score")
    println("Duration: ${(part1EndTime - part1StartTime) / 1000.0} seconds")
    println("\n------------------\n")
    println("Part 2:")
    val secondEvaluator = AmphipodEvaluator(lines2, 4)
    println(secondEvaluator.dump())
    val part2StartTime = System.currentTimeMillis()
    val (_, part2Score) = secondEvaluator.findLowestEnergy()
    val part2EndTime = System.currentTimeMillis()
    println("Minimum energy: $part2Score")
    println("Duration: ${(part2EndTime - part2StartTime) / 1000.0} seconds")
}

class AmphipodEvaluator(
    private val lines: List<String>,
    private val sideRoomDepth: Int,
) {
    private val hallwaySpaces = mutableListOf<Space>()
    private val eligibleHallwaySpaces = mutableListOf<Space>()
    private val sideRooms = mutableListOf<SideRoom>()
    private val initialLocations = mutableMapOf<Amphipod, Space>()
    private val initialOccupants = mutableMapOf<Space, Amphipod>()

    init {
        repeat(11) {
            val newSpace = Space(SpaceType.HALLWAY)
            hallwaySpaces.add(newSpace)
            if (it != 0) {
                hallwaySpaces[it - 1].neighbors.add(newSpace)
                newSpace.neighbors.add(hallwaySpaces[it - 1])
            }
        }
        repeat(4) { sideRoomNumber ->
            val connectedHallwaySpace = hallwaySpaces[(sideRoomNumber * 2) + 2]
            val spaces = listOf(2, 3, 4, 5)
                .take(sideRoomDepth)
                .map { typeForChar(lines[it][(sideRoomNumber * 2) + 3]) }
                .map { Amphipod(it) }
                .foldIndexed(mutableListOf<Space>()) { i, spaces, amphipod ->
                    val space = Space(SpaceType.SIDE_ROOM)
                    initialLocations[amphipod] = space
                    initialOccupants[space] = amphipod
                    if (i == 0) {
                        space.neighbors.add(connectedHallwaySpace)
                        connectedHallwaySpace.neighbors.add(space)
                    } else {
                        space.neighbors.add(spaces[i - 1])
                        spaces[i - 1].neighbors.add(space)
                    }
                    spaces.also { it.add(space) }
                }
            sideRooms.add(
                SideRoom(
                    spaces = spaces,
                    targetType = typeForChar(('A'..'D').elementAt(sideRoomNumber))
                )
            )
        }
        eligibleHallwaySpaces.addAll(
            hallwaySpaces.filter { space ->
                space.neighbors.all { neighbor -> neighbor.type != SpaceType.SIDE_ROOM }
            }
        )
    }

    fun findLowestEnergy() = findShortestPath(initialLocations, initialOccupants, 0, listOf())

    fun findShortestPath(
        locations: Map<Amphipod, Space>,
        occupants: Map<Space, Amphipod>,
        energy: Int,
        steps: List<String>
    ): Pair<List<String>, Int?> {
        val dump = dump(occupants)
        val newSteps = steps + dump
        val tree = Tree(locations, energy)
        if (tree in trees) return newSteps to trees[tree]
        if (occupants.values.all { it.currentState(locations, occupants, sideRooms) == AmphipodState.FINISHED }) {
            return newSteps to energy
        } else {
            val eligibleMoves = occupants
                .values
                .flatMap { amph ->
                    amph.eligibleMoves(locations, occupants, sideRooms, eligibleHallwaySpaces).map { amph to it }
                }
            if (eligibleMoves.isEmpty()) {
                return newSteps to null
            }
            val minScore = eligibleMoves
                .map { (amph, spaceToEnergy) ->
                    val (destination, requiredEnergy) = spaceToEnergy
                    val origin = locations[amph]!!
                    val newLocations = (locations - amph) + (amph to destination)
                    val newOccupants = (occupants - origin) + (destination to amph)
                    findShortestPath(
                        newLocations,
                        newOccupants,
                        energy + requiredEnergy,
                        newSteps,
                    )
                }
                .filter { (_, s) -> s != null }
                .takeUnless { it.isEmpty() }
                ?.minByOrNull { (_, s) -> s!! }
                ?: (listOf<String>() to null)
            return minScore.also { (_, score) -> trees[tree] = score }
        }
    }

    private val trees = mutableMapOf<Tree, Int?>()

    fun dump(occupants: Map<Space, Amphipod> = initialOccupants): String {
        var s = ("#############\n")
        val hallway = hallwaySpaces
            .map {
                val occupant = occupants[it]
                occupant?.type?.name?.first() ?: "."
            }
            .joinToString("")
        s += "#$hallway#\n"
        for (i in (0 until sideRoomDepth)) {
            val (a, b, c, d) = sideRooms
                .map { it.spaces[i] }
                .map {
                    val occupant = occupants[it]
                    occupant?.type?.name?.first() ?: "."
                }
            s += "###$a#$b#$c#$d###\n"
        }
        s += "  #########\n"
        return s
    }
}

data class Tree(
    val locations: Map<Amphipod, Space>,
    val energy: Int,
)

enum class AmphipodType(val energyRequirement: Int) {
    AMBER(1),
    BRONZE(10),
    COPPER(100),
    DESERT(1000)
}

fun typeForChar(ch: Char) =
    when (ch) {
        'A' -> AmphipodType.AMBER
        'B' -> AmphipodType.BRONZE
        'C' -> AmphipodType.COPPER
        'D' -> AmphipodType.DESERT
        else -> throw RuntimeException()
    }

enum class AmphipodState {
    NEED_TO_LEAVE,
    HALLWAY,
    FINISHED,
}

data class Amphipod(
    val type: AmphipodType,
    val id: String = UUID.randomUUID().toString()
) {
    fun currentState(
        locations: Map<Amphipod, Space>,
        occupants: Map<Space, Amphipod>,
        sideRooms: List<SideRoom>,
    ): AmphipodState {
        val location = locations[this]!!
        return when (location.type) {
            SpaceType.SIDE_ROOM -> {
                val sideRoom = sideRooms.single { it.containsSpace(location) }
                if (sideRoom.targetType != this.type) return AmphipodState.NEED_TO_LEAVE
                val sideRoomOccupants = sideRoom.spaces.mapNotNull { occupants[it] }
                return if (sideRoomOccupants.any { it.type != sideRoom.targetType }) {
                    AmphipodState.NEED_TO_LEAVE
                } else {
                    AmphipodState.FINISHED
                }
            }
            SpaceType.HALLWAY -> AmphipodState.HALLWAY
        }
    }

    fun eligibleMoves(
        locations: Map<Amphipod, Space>,
        occupants: Map<Space, Amphipod>,
        sideRooms: List<SideRoom>,
        eligibleHallwaySpaces: List<Space>,
    ): Map<Space, Int> {
        val targetSideRoom = sideRooms.single { it.targetType == this.type }
        val location = locations[this]!!
        val eligibleSpaces: List<Space> = when (currentState(locations, occupants, sideRooms)) {
            AmphipodState.FINISHED -> listOf()
            AmphipodState.HALLWAY -> {
                val targetOccupants = targetSideRoom.spaces.mapNotNull { occupants[it] }
                if (targetOccupants.any { it.type != this.type }) {
                    listOf()
                } else {
                    targetSideRoom.lastUnoccupied(occupants)?.let(::listOf) ?: listOf()
                }
            }
            AmphipodState.NEED_TO_LEAVE -> {
                val currentSideRoom = sideRooms.single { it.containsSpace(location) }
                val firstOccupied = currentSideRoom.firstOccupied(occupants)
                if (location != firstOccupied) {
                    listOf()
                } else {
                    eligibleHallwaySpaces.toList().filter { occupants[it] == null }
                }
            }
        }
        return eligibleSpaces.mapNotNull { space ->
            val cost = costToSpace(location, space, occupants.keys.toSet(), this.type.energyRequirement)
            if (cost == null) {
                null
            } else {
                space to (cost - this.type.energyRequirement)
            }
        }.toMap()
    }
}

data class Trip(
    val costPerStep: Int,
    val current: Space,
    val target: Space,
    val visited: Set<Space>,
)

private val trips = mutableMapOf<Trip, Int?>()

fun costToSpace(
    current: Space,
    target: Space,
    visited: Set<Space>,
    costPerStep: Int,
): Int? {
    if (current == target) {
        return costPerStep
    } else {
        val trip = Trip(
            costPerStep,
            current,
            target,
            visited
        )
        if (trip in trips) {
            return trips[trip]
        }
        val neighbors = current.neighbors.toSet() - visited
        val cost = if (neighbors.isEmpty()) {
            null
        } else {
            val newVisited = visited + current
            val nextCost = neighbors.mapNotNull {
                costToSpace(
                    it, target, newVisited, costPerStep
                )
            }
            if (nextCost.isEmpty()) {
                null
            } else {
                nextCost.single() + costPerStep
            }
        }
        trips[trip] = cost
        return cost
    }
}

enum class SpaceType {
    HALLWAY,
    SIDE_ROOM,
}

data class Space(
    val type: SpaceType,
    val id: String = UUID.randomUUID().toString()
) {
    val neighbors: MutableList<Space> = mutableListOf()
}

data class SideRoom(
    val spaces: List<Space>,
    val targetType: AmphipodType,
) {
    fun containsSpace(space: Space) = space in spaces

    fun firstOccupied(occupants: Map<Space, Amphipod>) =
        this.spaces.firstOrNull { occupants[it] != null }

    fun lastUnoccupied(occupants: Map<Space, Amphipod>) =
        this.spaces.lastOrNull { occupants[it] == null }
}
