package com.myth.playpick.domain.recommend

import com.myth.playpick.domain.model.Game

/**
 * RAWG lists editions and remasters as separate games, so rejecting "Cyberpunk 2077" left its
 * "Ultimate Edition" in the feed, and a series kept coming back one entry at a time. These keys
 * group them by name.
 */
object Variants {
    private val editionWords = Regex(
        "(?<![a-z])(the final cut|final cut|redux|game of the year|goty|complete|definitive|deluxe|enhanced|remastered|remaster|special|ultimate|" +
            "gold|premium|standard|digital|collector'?s|director'?s cut|anniversary|legendary|royal|hd|vr)(?![a-z])( edition)?",
    )
    private val marks = Regex("[™®©]")
    private val nonWord = Regex("[^a-z0-9]+")
    private val trailingNumber = Regex(" +([0-9]+|[ivx]+)$")

    /** The same game in every edition: "The Witcher 3: Wild Hunt – Complete Edition" and "The Witcher 3: Wild Hunt". */
    fun edition(name: String): String =
        name.lowercase().replace(marks, "").replace(editionWords, " ").replace(nonWord, " ").trim().replace(Regex(" +"), " ")

    /** The series a game belongs to: "The Witcher 3: Wild Hunt" and "The Witcher 2" are both "witcher". */
    fun franchise(name: String): String {
        val lower = name.lowercase().replace(marks, "")
        val title = lower.split(":", " - ", " – ").first()
        return edition(title).replace(trailingNumber, "").removePrefix("the ").trim()
    }
}

val Game.editionKey: String get() = Variants.edition(name)
val Game.franchiseKey: String get() = Variants.franchise(name)
