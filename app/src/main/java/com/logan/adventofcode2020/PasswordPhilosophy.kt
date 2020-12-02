package com.logan.adventofcode2020

import java.io.File
import java.security.Policy

fun main() {
    val inputFileName = "input/day2_input.txt"
    val input = readInputStringsFromFile(inputFileName)

    val validSledPasswords = processPasswords(input, "sled")
    val validTobogganPasswords = processPasswords(input, "toboggan")

    println("Valid Sled Passwords: ${validSledPasswords.count()}")
    println("Valid Toboggan Passwords: ${validTobogganPasswords.count()}")
}

fun readInputStringsFromFile(name: String): List<String> {
    var input: MutableList<String> = arrayListOf()
    File(name).forEachLine { input.add(it) }
    return input
}

fun processPasswords(passwords: List<String>, passwordPolicy: String): List<String> {
    var validPasswords  = mutableListOf<String>()

    passwords.forEach {
        var policy = extractPasswordPolicy(it)
        var password = extractPassword(it)

        var result = when(passwordPolicy) {
            "sled"-> checkSledPasswordValidity(password, policy)
            "toboggan" -> checkTobogganPasswordValidity(password, policy)
            else -> false
        }

        if (result) {
            validPasswords.add(it)
        }
    }

    return validPasswords
}

fun extractPasswordPolicy(password: String): Triple<Int, Int, String> {
    val extractedPolicy = password.split("-"," ",":")

    return Triple(extractedPolicy[0].toInt(), extractedPolicy[1].toInt(), extractedPolicy[2])
}

fun extractPassword(passwordString: String): String {
    return passwordString.split(": ").last()
}

fun checkSledPasswordValidity(password: String, policy: Triple<Int, Int, String>): Boolean {
    val lowerBound = policy.first
    val upperBound = policy.second
    val letter = policy.third.toCharArray().first()

    val letterCount = password.filter { it == letter }.count()

    return (letterCount in lowerBound..upperBound)
}

fun checkTobogganPasswordValidity(password: String, policy: Triple<Int, Int, String>): Boolean {
    val firstIndex = policy.first-1
    val secondIndex = policy.second-1
    val letter = policy.third.toCharArray().first()

    return ((password[firstIndex] == letter) xor (password[secondIndex] == letter))
}