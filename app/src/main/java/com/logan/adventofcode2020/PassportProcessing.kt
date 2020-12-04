package com.logan.adventofcode2020

import java.io.File
import kotlin.math.abs
import kotlin.math.log10

//byr (Birth Year) - four digits; at least 1920 and at most 2002.
//iyr (Issue Year) - four digits; at least 2010 and at most 2020.
//eyr (Expiration Year) - four digits; at least 2020 and at most 2030.
//hgt (Height) - a number followed by either cm or in:
//If cm, the number must be at least 150 and at most 193.
//If in, the number must be at least 59 and at most 76.
//hcl (Hair Color) - a # followed by exactly six characters 0-9 or a-f.
//ecl (Eye Color) - exactly one of: amb blu brn gry grn hzl oth.
//pid (Passport ID) - a nine-digit number, including leading zeroes.
//cid (Country ID) - ignored, missing or not.

data class Passport(
        var birthYear: Int? = null,
        var issueYear: Int? = null,
        var expirationYear: Int? = null,
        var height: String? = null,
        var hairColor: String? = null,
        var eyeColor: String? = null,
        var passportID: String? = null,
        var countryID: String? = null
) {
    fun isModifiedValid(): Boolean {
        return (birthYearIsValid()
                && issueYearIsValid()
                && expirationYearIsValid()
                && heightIsValid()
                && hairColorIsValid()
                && eyeColorIsValid()
                && passportIDIsValid())
    }

    fun isValid(): Boolean {
        return isModifiedValid() && countryIDIsValid()
    }

    private fun birthYearIsValid(): Boolean {
        return birthYear?.checkValidYear(4, 1920, 2002) ?: false
    }

    private fun issueYearIsValid(): Boolean {
        return issueYear?.checkValidYear(4, 2010, 2020) ?: false
    }

    private fun expirationYearIsValid(): Boolean {
        return expirationYear?.checkValidYear(4, 2020, 2030) ?: false
    }

    private fun heightIsValid(): Boolean {
        if (height == null) { return false }
        val unit = height!!.subSequence(height!!.lastIndex-1, height!!.lastIndex+1)
        val value = height!!.substringBefore(unit.toString()).toInt()

        return when (unit) {
            "cm" -> (value in 150..193)
            "in" -> (value in 59..76)
            else -> false
        }
    }

    private fun hairColorIsValid(): Boolean {
        if (hairColor == null) { return false }

        val regex = """#[0-9a-f]{6}""".toRegex()
        return hairColor!!.matches(regex)
    }

    private fun eyeColorIsValid(): Boolean {
        if (eyeColor == null) { return false }

        return when (eyeColor) {
            "amb", "blu", "brn", "gry", "grn", "hzl", "oth" -> true
            else -> false
        }
    }

    private fun passportIDIsValid(): Boolean {
        if (passportID == null) { return false }

        val regex = """[\d]{9}""".toRegex()
        return passportID!!.matches(regex)
    }

    private fun countryIDIsValid(): Boolean {
        return countryID != null
    }
}

fun Int.length() = when(this) {
    0 -> 1
    else -> log10(abs(toDouble())).toInt() + 1
}

fun Int.checkValidYear(length: Int, lowerBound: Int, upperBound: Int): Boolean {
    return (this.length() == length && (this in lowerBound..upperBound))
}

enum class PassportCode {
    BYR {
        override fun code(): String = "byr"
    },
    IYR {
        override fun code(): String = "iyr"
    },
    EYR {
        override fun code(): String = "eyr"
    },
    HGT {
        override fun code(): String = "hgt"
    },
    HCL {
        override fun code(): String = "hcl"
    },
    ECL {
        override fun code(): String = "ecl"
    },
    PID {
        override fun code(): String = "pid"
    },
    CID {
        override fun code(): String = "cid"
    };

    abstract fun code(): String
}


fun main() {
    val inputFileName = "input/day4_input.txt"
    val passports = readPassportsFromFile(inputFileName)

    println("Total: ${passports.count()}")
    println("Total Valid (modified): ${countValidPassports(passports, true)}")
    println("Total Valid: ${countValidPassports(passports, false)}")
}

fun readPassportsFromFile(name: String): MutableList<Passport> {
    val lines = File(name).readLines()
    return parsePassports(lines)
}

fun parsePassports(lines: List<String>): MutableList<Passport> {
    val passports = mutableListOf<Passport>()
    var currentPassport = Passport()

    lines.forEach { line ->
        val codeValuePairs = line.split(" ")
        codeValuePairs.forEach {
            val pair = it.split(':')

            when (pair.first()) {
                PassportCode.BYR.code() -> currentPassport.birthYear = pair[1].toInt()
                PassportCode.IYR.code() -> currentPassport.issueYear = pair[1].toInt()
                PassportCode.EYR.code() -> currentPassport.expirationYear = pair[1].toInt()
                PassportCode.HGT.code() -> currentPassport.height = pair[1]
                PassportCode.HCL.code() -> currentPassport.hairColor = pair[1]
                PassportCode.ECL.code() -> currentPassport.eyeColor = pair[1]
                PassportCode.PID.code() -> currentPassport.passportID = pair[1]
                PassportCode.CID.code() -> currentPassport.countryID = pair[1]
            }
        }

        if (line.isBlank()) {
            passports.add(currentPassport)
            currentPassport = Passport()
        }
    }

    passports.add((currentPassport))

    return passports
}

fun countValidPassports(passports: List<Passport>, modified: Boolean): Int {
    return if (modified) {
        passports.count { it.isModifiedValid() }
    } else {
        passports.count { it.isValid() }
    }
}

