package com.logan.adventofcode2020

import java.io.File
import kotlin.math.absoluteValue


enum class Navigation(val action: Char) {
    N('N'),
    S('S'),
    E('E'),
    W('W'),
    L('L'),
    R('R'),
    F('F')
}

var position = Pair<Int, Int>(0,0)
var waypoint = Pair<Int, Int>(1,10)
var direction = 0

fun main() {
    val inputFileName = "input/day12_input.txt"
    val instructions = readNavigationInstructionsFromFile(inputFileName)

    navigateShip(instructions, false)
    println("Manhattan Distance: ${manhattanDistance()}")

    position = Pair(0,0)
    navigateShip(instructions, true)
    println("Manhattan Distance: ${manhattanDistance()}")
}

fun readNavigationInstructionsFromFile(name: String): List<Pair<Char, Int>> {
    val instructions = mutableListOf<Pair<Char, Int>>()
    val lines = File(name).readLines()

    lines.forEach {
        instructions.add(Pair(it[0].toChar(), it.substring(1).toInt()))
    }

    return instructions
}

fun navigateShip(instructions: List<Pair<Char, Int>>, waypoint: Boolean) {
    instructions.forEach {
        if (!waypoint) { handleInstruction(it) }
        else { handleInstructionsWithWaypoint(it) }
    }
}

fun handleInstruction(instruction: Pair<Char, Int>) {
    when (instruction.first) {
        Navigation.N.action -> position = Pair<Int, Int>(position.first+instruction.second, position.second)
        Navigation.S.action -> position = Pair<Int, Int>(position.first-instruction.second, position.second)
        Navigation.E.action -> position = Pair<Int, Int>(position.first, position.second+instruction.second)
        Navigation.W.action -> position = Pair<Int, Int>(position.first, position.second-instruction.second)
        Navigation.L.action,
        Navigation.R.action -> changeDirection(instruction)
        Navigation.F.action -> moveForward(instruction.second)
    }
}

fun changeDirection(instruction: Pair<Char, Int>) {
    var rotation = instruction.second

    if (instruction.first == Navigation.R.action) {
        rotation *= -1
    }

    direction = (direction + rotation) % 360
    if (direction < 0) { direction += 360 }
}

fun moveForward(distance: Int) {
    when (direction) {
        0 -> position = Pair<Int, Int>(position.first, position.second+distance)
        90 -> position = Pair<Int, Int>(position.first+distance, position.second)
        180 -> position = Pair<Int, Int>(position.first, position.second-distance)
        270 -> position = Pair<Int, Int>(position.first-distance, position.second)
    }
}

fun handleInstructionsWithWaypoint(instruction: Pair<Char, Int>) {
    when (instruction.first) {
        Navigation.N.action,
        Navigation.S.action,
        Navigation.E.action,
        Navigation.W.action -> moveWaypoint(instruction)
        Navigation.L.action,
        Navigation.R.action -> rotateWaypoint(instruction)
        Navigation.F.action -> moveToWaypoint(instruction.second)
    }
}

fun rotateWaypoint(instruction: Pair<Char, Int>) {
    var rotation = instruction.second

    if (instruction.first == Navigation.L.action) {
        rotation = 360 - rotation
    }

    when (rotation) {
        90 -> waypoint = Pair(-waypoint.second, waypoint.first)
        180 -> waypoint = Pair(-waypoint.first, -waypoint.second)
        270 -> waypoint = Pair(waypoint.second, -waypoint.first)
        360 -> waypoint = waypoint
    }
}

fun moveToWaypoint(multiple: Int) {
    for (i in 0 until multiple) {
        position = Pair(position.first + waypoint.first, position.second + waypoint.second)
    }
}

fun moveWaypoint(instruction: Pair<Char, Int>) {
    when (instruction.first) {
        Navigation.N.action -> waypoint = Pair(waypoint.first+instruction.second, waypoint.second)
        Navigation.S.action -> waypoint = Pair(waypoint.first-instruction.second, waypoint.second)
        Navigation.E.action -> waypoint = Pair(waypoint.first, waypoint.second+instruction.second)
        Navigation.W.action -> waypoint = Pair(waypoint.first, waypoint.second-instruction.second)
    }
}

fun manhattanDistance(): Int {
    return (position.first.absoluteValue + position.second.absoluteValue)
}