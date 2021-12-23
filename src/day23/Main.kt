package com.zpthacker.aoc21.day23

import java.lang.RuntimeException
import java.util.*

fun main() {
    var input = """
        #############
        #...........#
        ###B#C#B#D###
          #A#D#C#A#
          #########
    """.trimIndent()
//    input = getInput(23)
    val lines = input.trim().split("\n")
    println("hello")
    val hallwaySpaces = mutableListOf<Space>()
    repeat(11) {
        val newSpace = Space(SpaceType.HALLWAY, null)
        hallwaySpaces.add(newSpace)
        if (it != 0) {
            hallwaySpaces[it-1].neighbors.add(newSpace)
            newSpace.neighbors.add(hallwaySpaces[it-1])
        }
    }
    val sideRooms = mutableListOf<SideRoom>()
    repeat(4) {
        val connectedHallwaySpace = hallwaySpaces[(it * 2) + 2]
        val firstAmphipodTypeChar = lines[2][(it * 2) + 3]
        val firstAmphipodType = when (firstAmphipodTypeChar) {
            'A' -> AmphipodType.AMBER
            'B' -> AmphipodType.BRONZE
            'C' -> AmphipodType.COPPER
            'D' -> AmphipodType.DESERT
            else -> throw RuntimeException()
        }
        val secondAmphipodTypeChar = lines[3][(it * 2) + 3]
        val secondAmphipodType = when (secondAmphipodTypeChar) {
            'A' -> AmphipodType.AMBER
            'B' -> AmphipodType.BRONZE
            'C' -> AmphipodType.COPPER
            'D' -> AmphipodType.DESERT
            else -> throw RuntimeException()
        }
        val firstSpace = Space(SpaceType.SIDE_ROOM)
        val firstAmphipod = Amphipod(firstAmphipodType, firstSpace)
        firstSpace.occupant = firstAmphipod
        firstSpace.neighbors.add(connectedHallwaySpace)
        connectedHallwaySpace.neighbors.add(firstSpace)
        val secondSpace = Space(SpaceType.SIDE_ROOM)
        val secondAmphipod = Amphipod(secondAmphipodType, secondSpace)
        secondSpace.occupant = secondAmphipod
        secondSpace.neighbors.add(firstSpace)
        firstSpace.neighbors.add(secondSpace)
        sideRooms.add(
            SideRoom(
                spaces = listOf(firstSpace, secondSpace),
                targetAmphipodType = when (it) {
                    0 -> AmphipodType.AMBER
                    1 -> AmphipodType.BRONZE
                    2 -> AmphipodType.COPPER
                    3 -> AmphipodType.DESERT
                    else -> throw RuntimeException()
                }
            )
        )
    }
}

class Burrow(
    val hallwaySpaces: List<Space>,
    val sideRooms: List<SideRoom>,
    val amphipods: List<Amphipod>,
) {
    val eligibleHallwaySpaces = hallwaySpaces.filter { hallwaySpace ->
        hallwaySpace.neighbors.any { it.type == SpaceType.SIDE_ROOM }
    }

    fun doIt() {
        amphipods.forEach {

        }
    }
}

class SideRoom(
    val spaces: List<Space>,
    val targetAmphipodType: AmphipodType,
)

enum class SpaceType {
    HALLWAY,
    SIDE_ROOM,
}

class Space(
    val type: SpaceType,
    var occupant: Amphipod? = null,
) {
    val id = UUID.randomUUID().toString()
    var neighbors: MutableList<Space> = mutableListOf()

    override fun equals(other: Any?): Boolean {
        if (other !is Space) return false
        return other.id == id
    }
}

class Amphipod(
    val type: AmphipodType,
    val space: Space,
) {
    val id = UUID.randomUUID().toString()

    fun eligibleMoves(eligibleHallwaySpaces: List<Space>, sideRooms: List<SideRoom>): List<Pair<Space, Int>> {
        if (space.type == SpaceType.SIDE_ROOM) {
            val targetSideRoom = sideRooms.single { it.targetAmphipodType == type }
            val otherSideRoomSpace = space.neighbors.single { it.type == SpaceType.SIDE_ROOM }
            if (otherSideRoomSpace.occupant?.type == this.type && this.space in targetSideRoom.spaces) {
                return listOf()
            }
        }
    }
}

enum class AmphipodType(
    val energyRequirement: Int
) {
    AMBER(1),
    BRONZE(10),
    COPPER(100),
    DESERT(1000),
}

