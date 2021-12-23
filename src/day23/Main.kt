package com.zpthacker.aoc21.day23

import com.zpthacker.aoc21.getInput
import java.util.*

private val hallwaySpaces = mutableListOf<Space>()
private val eligibleHallwaySpaces = mutableListOf<Space>()
private val sideRooms = mutableListOf<SideRoom>()

private var id = ""

fun main() {
    var input = """
        #############
        #...........#
        ###B#C#B#D###
          #D#C#B#A#
          #D#B#A#C#
          #A#D#C#A#
          #########
    """.trimIndent()
    input = getInput(23)
    val lines = input.trim().split("\n")
    repeat(11) {
        val newSpace = Space(SpaceType.HALLWAY)
        hallwaySpaces.add(newSpace)
        if (it != 0) {
            hallwaySpaces[it - 1].neighbors.add(newSpace)
            newSpace.neighbors.add(hallwaySpaces[it - 1])
        }
    }
    val locations = mutableMapOf<Amphipod, Space>()
    val occupants = mutableMapOf<Space, Amphipod>()
    repeat(4) {
        val connectedHallwaySpace = hallwaySpaces[(it * 2) + 2]
        val firstAmphipodTypeChar = lines[2][(it * 2) + 3]
        val firstAmphipodType = typeForChar(firstAmphipodTypeChar)
        val secondAmphipodTypeChar = lines[3][(it * 2) + 3]
        val secondAmphipodType = typeForChar(secondAmphipodTypeChar)
        val thirdAmphipodTypeChar = lines[4][(it * 2) + 3]
        val thirdAmphipodType = typeForChar(thirdAmphipodTypeChar)
        val fourthAmphipodTypeChar = lines[5][(it * 2) + 3]
        val fourthAmphipodType = typeForChar(fourthAmphipodTypeChar)
        val firstSpace = Space(SpaceType.SIDE_ROOM)
        val firstAmphipod = Amphipod(firstAmphipodType)
        locations[firstAmphipod] = firstSpace
        occupants[firstSpace] = firstAmphipod
        firstSpace.neighbors.add(connectedHallwaySpace)
        connectedHallwaySpace.neighbors.add(firstSpace)
        val secondSpace = Space(SpaceType.SIDE_ROOM)
        val secondAmphipod = Amphipod(secondAmphipodType)
        if (it == 3) {
            id = secondAmphipod.id
        }
        locations[secondAmphipod] = secondSpace
        occupants[secondSpace] = secondAmphipod
        secondSpace.neighbors.add(firstSpace)
        firstSpace.neighbors.add(secondSpace)
        val thirdSpace = Space(SpaceType.SIDE_ROOM)
        val thirdAmphipod = Amphipod(thirdAmphipodType)
        locations[thirdAmphipod] = thirdSpace
        occupants[thirdSpace] = thirdAmphipod
        thirdSpace.neighbors.add(secondSpace)
        secondSpace.neighbors.add(thirdSpace)
        val fourthSpace = Space(SpaceType.SIDE_ROOM)
        val fourthAmphipod = Amphipod(fourthAmphipodType)
        locations[fourthAmphipod] = fourthSpace
        occupants[fourthSpace] = fourthAmphipod
        fourthSpace.neighbors.add(thirdSpace)
        thirdSpace.neighbors.add(fourthSpace)
        sideRooms.add(
            SideRoom(
                firstSpace = firstSpace,
                secondSpace = secondSpace,
                thirdSpace = thirdSpace,
                fourthSpace = fourthSpace,
                targetType = typeForChar(('A'..'D').elementAt(it))
            )
        )
    }
    println(dump(occupants))
    eligibleHallwaySpaces.addAll(hallwaySpaces.filter { space -> space.neighbors.all { neighbor -> neighbor.type != SpaceType.SIDE_ROOM } })
    val (steps, score) = findShortestPath(locations, occupants, 0, listOf())
    steps.forEach {
        println(it)
        println("------")
    }
    println(score)

    println("foo")
}

data class Tree(
    val locations: Map<Amphipod, Space>,
    val energy: Int,
)

private val trees = mutableMapOf<Tree, Int?>()

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
    if (occupants.values.all { it.currentState(locations, occupants) == AmphipodState.FINISHED }) {
        return newSteps to energy
    } else {
        val eligibleMoves = occupants
            .values
            .flatMap { amph ->
                amph.eligibleMoves(locations, occupants).map { amph to it }
            }
        if (eligibleMoves.isEmpty()) {
            return newSteps to null
        }
        val allScores = eligibleMoves
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
        when (steps.count()) {
            1 -> println("Finished a top level move")
        }
        val minScore = allScores
            ?.minByOrNull { (_, s) -> s!! }
            ?: (listOf<String>() to null)
        trees[tree] = minScore.second
        return minScore
    }
}

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
    fun currentState(locations: Map<Amphipod, Space>, occupants: Map<Space, Amphipod>): AmphipodState {
        val location = locations[this]!!
        return when (location.type) {
            SpaceType.SIDE_ROOM -> {
                val sideRoom = sideRooms.single { it.containsSpace(location) }
                if (sideRoom.targetType != this.type) return AmphipodState.NEED_TO_LEAVE
                val occupants = sideRoom.spaces.mapNotNull { occupants[it] }
                return if (occupants.any { it.type != sideRoom.targetType }) {
                    AmphipodState.NEED_TO_LEAVE
                } else {
                    AmphipodState.FINISHED
                }
            }
            SpaceType.HALLWAY -> AmphipodState.HALLWAY
        }
    }

    fun eligibleMoves(locations: Map<Amphipod, Space>, occupants: Map<Space, Amphipod>): Map<Space, Int> {
        val targetSideRoom = sideRooms.single { it.targetType == this.type }
        val location = locations[this]!!
        val eligibleSpaces: List<Space> = when (currentState(locations, occupants)) {
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
    val firstSpace: Space,
    val secondSpace: Space,
    val thirdSpace: Space,
    val fourthSpace: Space,
    val targetType: AmphipodType,
) {
    fun containsSpace(space: Space) = space in spaces

    fun firstOccupied(occupants: Map<Space, Amphipod>) =
        this.spaces.firstOrNull { occupants[it] != null }

    fun lastUnoccupied(occupants: Map<Space, Amphipod>) =
        this.spaces.lastOrNull { occupants[it] == null }

    val spaces = listOf(firstSpace, secondSpace, thirdSpace, fourthSpace)
}

fun dump(occupants: Map<Space, Amphipod>): String {
    var s = ("#############\n")
    val hallway = hallwaySpaces
        .map {
            val occupant = occupants[it]
            occupant?.type?.name?.first() ?: "."
        }
        .joinToString("")
    s += "#$hallway#\n"
    val (a, b, c, d) = sideRooms
        .map(SideRoom::firstSpace)
        .map {
            val occupant = occupants[it]
            occupant?.type?.name?.first() ?: "."
        }
    s += "###$a#$b#$c#$d###\n"
    val (w, x, y, z) = sideRooms
        .map(SideRoom::secondSpace)
        .map {
            val occupant = occupants[it]
            occupant?.type?.name?.first() ?: "."
        }
    s += "  #$w#$x#$y#$z#\n"
    val (e, f, g, h) = sideRooms
        .map(SideRoom::thirdSpace)
        .map {
            val occupant = occupants[it]
            occupant?.type?.name?.first() ?: "."
        }
    s += "  #$e#$f#$g#$h#\n"
    val (m, n, o, p) = sideRooms
        .map(SideRoom::fourthSpace)
        .map {
            val occupant = occupants[it]
            occupant?.type?.name?.first() ?: "."
        }
    s += "  #$m#$n#$o#$p#\n"
    s += "  #########\n"
    return s
}