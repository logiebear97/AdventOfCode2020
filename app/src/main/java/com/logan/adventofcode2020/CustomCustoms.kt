package com.logan.adventofcode2020

import java.io.File

data class CustomsForm(val survey: List<Boolean>) {
    fun getSurveyTotal(): Int {
        return survey.count { it }
    }
}

fun main() {
    val inputFileName = "input/day6_input.txt"
    val forms = readSurveysFromFile(inputFileName)

    println("Sum: ${getSumOfAllSurveys(forms)}")
}

fun readSurveysFromFile(name: String): MutableList<CustomsForm> {
    var forms = mutableListOf<CustomsForm>()
    val lines = File(name).readLines()

    var currentForm = BooleanArray(26)
    var currentSharedAnswers = mutableListOf<Char>()
    var firstMember = true
    lines.forEach { line ->
        if (line.isBlank()) {
            currentSharedAnswers.forEach { currentForm[it.toInt()-97] = true }
            forms.add(CustomsForm(currentForm.toList()))

            currentForm = BooleanArray(26)
            currentSharedAnswers = mutableListOf()
            firstMember = true
            return@forEach
        }

        if (firstMember) {
            line.forEach { currentSharedAnswers.add(it) }
        } else {
            currentSharedAnswers.removeIf {
                !line.contains(it)
            }
        }

        firstMember = false
    }

    currentSharedAnswers.forEach { currentForm[it.toInt()-97] = true }
    forms.add(CustomsForm(currentForm.toList()))

    return forms
}

fun getSumOfAllSurveys(forms: MutableList<CustomsForm>): Int {
    var sum = 0
    forms.forEach { sum += it.getSurveyTotal() }

    return sum
}