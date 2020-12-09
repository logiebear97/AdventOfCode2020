package com.logan.adventofcode2020

import java.io.File

enum class Instruction(val operation: String) {
    ACC("acc"),
    JMP("jmp"),
    NOP("nop")
}

var acc = 0
var insPointer = 0

fun main() {
    val inputFileName = "input/day8_input.txt"
    val instructions = readInstructionsFromFile(inputFileName)

    executeInstructionsOnce(instructions)

    val fixedInstructions = fixCorruptedInstruction(instructions)
    println("acc = $acc")
}

fun readInstructionsFromFile(name: String): List<Pair<String, Int>> {
    val instructions = mutableListOf<Pair<String, Int>>()
    val lines = File(name).readLines()

    lines.forEach {
        val comps = it.split(" ")
        var ins = comps[0]

        var num = comps[1].subSequence(1, comps[1].length).toString().toInt()
        var pos = comps[1].subSequence(0,1)
        if (pos == "-") { num *= -1  }

        instructions.add(Pair(ins, num))
    }

    return instructions
}

fun executeInstructionsOnce(instructions: List<Pair<String, Int>>) {
    val executedInstructions = BooleanArray(instructions.size)

    acc = 0
    insPointer = 0

    while (!executedInstructions[insPointer]) {
        executedInstructions[insPointer] = true
        when (instructions[insPointer].first) {
            Instruction.ACC.operation -> accumulate(instructions[insPointer].second)
            Instruction.JMP.operation -> jump(instructions[insPointer].second)
            Instruction.NOP.operation -> noOperation()
        }
    }

    println("acc = $acc")
}

fun fixCorruptedInstruction(instructions: List<Pair<String, Int>>): List<Pair<String, Int>> {
    var newInstructions = instructions.toMutableList()

    instructions.forEach {
        var newInstruction = Pair<String, Int>("", 0)

        when (it.first) {
            Instruction.ACC.operation -> return@forEach
            Instruction.JMP.operation -> newInstruction = Pair("nop", it.second)
            Instruction.NOP.operation -> newInstruction = Pair("jmp", it.second)
        }

        newInstructions[instructions.indexOf(it)] = newInstruction
        if (willCodeTerminate(newInstructions)) {
            return newInstructions
        } else {
            newInstructions[instructions.indexOf(it)] = it
        }
    }

    return newInstructions
}

fun willCodeTerminate(instructions: List<Pair<String, Int>>): Boolean {
    var terminated = false
    val executedInstructions = BooleanArray(instructions.size)

    acc = 0
    insPointer = 0

    while (!terminated && !executedInstructions[insPointer]) {
        executedInstructions[insPointer] = true

        when (instructions[insPointer].first) {
            Instruction.ACC.operation -> accumulate(instructions[insPointer].second)
            Instruction.JMP.operation -> jump(instructions[insPointer].second)
            Instruction.NOP.operation -> noOperation()
        }

        if (insPointer == instructions.size) {
            terminated = true
        }
    }

    return terminated
}

fun accumulate(value: Int) {
    acc += value
    insPointer += 1
}

fun jump(value: Int) {
    insPointer += value
}

fun noOperation() {
    insPointer += 1
}

