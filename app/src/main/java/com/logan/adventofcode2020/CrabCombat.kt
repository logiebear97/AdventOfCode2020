package com.logan.adventofcode2020

import java.io.File

var playerOne = mutableListOf<Int>()
var playerTwo = mutableListOf<Int>()

var winner = mutableListOf<Int>()

var game = 0

fun main() {
    val inputFileName = "input/day22_input.txt"
    readDecksFromFile(inputFileName)
//    playCombat()
//    println("Winner's Score: ${calculateScore()}")

    val winningDeck = playRecursiveCombat(playerOne, playerTwo)
    println("Winner's Score: ${calculateScore(winningDeck.second)}")
}

fun readDecksFromFile(name: String) {
    val lines = File(name).readLines()

    var pOneActive = false

    lines.forEach { line ->
        if (line.isEmpty()) {
            return@forEach
        }

        if (line.contains("Player")) {
            pOneActive = !pOneActive
            return@forEach
        }

        val card = line.toInt()

        when (pOneActive) {
            true -> playerOne.add(card)
            false -> playerTwo.add(card)
        }
    }
}

fun playCombat() {
    while (playerOne.isNotEmpty() && playerTwo.isNotEmpty()) {
        val p1 = playerOne.first()
        playerOne.removeFirst()

        val p2 = playerTwo.first()
        playerTwo.removeFirst()

        if (p1 > p2) {
            playerOne.add(p1)
            playerOne.add(p2)
        } else {
            playerTwo.add(p2)
            playerTwo.add(p1)
        }
    }

    winner = if (playerOne.isNotEmpty()) {
        playerOne
    } else {
        playerTwo
    }
}

fun playRecursiveCombat(pOne: MutableList<Int>, pTwo: MutableList<Int>): Pair<Boolean, MutableList<Int>> {
    var playerOneDeck = pOne.toMutableList()
    var playerTwoDeck = pTwo.toMutableList()

    var pOnePrevious = mutableListOf<MutableList<Int>>()
    var pTwoPrevious = mutableListOf<MutableList<Int>>()

    println("=== Game ${++game} ===\n")

    var round = 1

    while (playerOneDeck.isNotEmpty() && playerTwoDeck.isNotEmpty()) {
        if (pOnePrevious.contains(playerOneDeck) && pTwoPrevious.contains(playerTwoDeck)) {
            return Pair(true, playerOneDeck)
        }

        pOnePrevious.add(playerOneDeck.toMutableList())
        pTwoPrevious.add(playerTwoDeck.toMutableList())

        println("-- Round $round (Game $game) --")
        println("Player 1 Deck: ${playerOneDeck.toString()}")
        println("Player 2 Deck: ${playerTwoDeck.toString()}")

        val p1 = playerOneDeck.first()
        playerOneDeck.removeFirst()

        val p2 = playerTwoDeck.first()
        playerTwoDeck.removeFirst()

        println("Player 1 plays: $p1")
        println("Player 2 plays: $p2")

        if (playerOneDeck.size >= p1 && playerTwoDeck.size >= p2) {
            var pOneSubDeck = playerOneDeck.subList(0, p1)
            var pTwoSubDeck = playerTwoDeck.subList(0, p2)

            println("Playing a sub-game to determine the winner...")
            val winner = playRecursiveCombat(pOneSubDeck, pTwoSubDeck).first

            if (winner) {
                println("Player 1 wins round ${round++} of game $game!\n")
                playerOneDeck.add(p1)
                playerOneDeck.add(p2)
            } else {
                println("Player 2 wins round ${round++} of game $game!\n")
                playerTwoDeck.add(p2)
                playerTwoDeck.add(p1)
            }
        } else {
            if (p1 > p2) {
                println("Player 1 wins round ${round++} of game $game!\n")
                playerOneDeck.add(p1)
                playerOneDeck.add(p2)
            } else {
                println("Player 2 wins round ${round++} of game $game!\n")
                playerTwoDeck.add(p2)
                playerTwoDeck.add(p1)
            }
        }
    }

    val winningDeck = if (playerOneDeck.isNotEmpty()) {
        println("The winner of game $game is Player 1!\n")
        playerOneDeck
    } else {
        println("The winner of game $game is Player 2!\n")
        playerTwoDeck
    }

    if (game > 1) {
        println("...anyway, back to game ${--game}.")
    }
    return Pair(playerOneDeck.isNotEmpty(), winningDeck)
}

fun calculateScore(winningDeck: MutableList<Int>): Int {
    var score = 0

    for (i in winningDeck.indices) {
        score += winningDeck[i] * (winningDeck.size-i)
    }

    return score
}