package com.logan.adventofcode2020

import java.io.File

typealias Ingredient = String
typealias Allergen = String

var allergens = mutableListOf<Pair<Allergen, Ingredient>>()
var ingredients = mutableListOf<Ingredient>()

var ingredientCounts = mutableMapOf<Ingredient, Int>()
var foods = mutableListOf<Pair<MutableList<Ingredient>, MutableList<Allergen>>>()

fun main() {
    val inputFileName = "input/day21_input.txt"
    readFoodListFromFile(inputFileName)
    narrowDownAllergens()

    println(allergens.toString())
    println(ingredients.toString())

    println("Non-allergen count: ${countNonAllergenIngredients()}")
    printListOfAllergens()
}

fun readFoodListFromFile(name: String) {
    val lines = File(name).readLines()

    lines.forEach { line ->
        var foodAllergens = mutableListOf<Allergen>()
        var foodIngredients = mutableListOf<Ingredient>()

        //add all ingredients as potential allergens
        var allergenList = line.substringAfter("contains ")
        allergenList.split( ", ", ")").forEach { allergen ->
            if (allergen.isEmpty()) return@forEach
            foodAllergens.add(allergen)
        }

        var ingredientsList = line.substringBefore("(").split(" ")
        ingredientsList.forEach { ingr ->
            if (ingr == "") return@forEach
            if (!ingredients.contains(ingr)) ingredients.add(ingr)
            foodIngredients.add(ingr)

            //add ingredient to count map
            if (!ingredientCounts.contains(ingr)) ingredientCounts[ingr] = 0
        }

        foods.add(Pair(foodIngredients, foodAllergens))
    }
}

fun narrowDownAllergens() {
    //copy the foods
    var foodList = mutableListOf<Pair<MutableList<Ingredient>, MutableList<Allergen>>>()
    for (food in foods) {
        foodList.add(food.copy())
    }

    while (foodList.isNotEmpty() && foodList.count { it.second.size > 0 } != 0) {
        var foodWithOneAllergen = foodList.first { it.second.size == 1 }
        foodList.remove(foodWithOneAllergen)

        var allergen = foodWithOneAllergen.second.first()
        var foodsWithAllergen = foodList.filter { it.second.contains(allergen) }
        var possibleIngredients = mutableListOf<Ingredient>()

        for (ingredient in foodWithOneAllergen.first) {
            if (foodsWithAllergen.all { it.first.contains(ingredient) }) {
                possibleIngredients.add(ingredient)
            }
        }

        //found the allergen
        if (possibleIngredients.size == 1) {
            //remove ingredient from all other foods
            for (food in foodList.filter { it.first.contains(possibleIngredients.first()) }) {
                food.first.remove(possibleIngredients.first())
            }

            //remove allergen from all other foods
            for (food in foodList.filter { it.second.contains(allergen) })  {
                food.second.remove(allergen)
            }

            allergens.add(Pair(allergen, possibleIngredients.first()))
            ingredientCounts.remove(possibleIngredients.first())
        } else {
            foodList.add(foodWithOneAllergen)
        }
    }

    //count ingredients
    for (ingredient in ingredientCounts.keys) {
        ingredientCounts[ingredient] = foods.count { it.first.contains(ingredient) }
    }
}

fun countNonAllergenIngredients(): Int {
    return ingredientCounts.values.sum()
}

fun printListOfAllergens() {
    var allergenNames = mutableListOf<Ingredient>()
    allergens.sortBy { it.first }
    for (allergen in allergens) {
        allergenNames.add(allergen.second)
    }
    for (allergen in allergenNames) {
        print("$allergen,")
    }
}