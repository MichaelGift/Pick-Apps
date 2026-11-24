package com.myth.playpick.domain.recommend

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class VariantsTest {

    @Test
    fun `editions of one game share a key`() {
        assertEquals(Variants.edition("Cyberpunk 2077"), Variants.edition("Cyberpunk 2077: Ultimate Edition"))
        assertEquals(Variants.edition("The Witcher 3: Wild Hunt"), Variants.edition("The Witcher 3: Wild Hunt – Game of the Year Edition"))
        assertEquals(Variants.edition("Skyrim"), Variants.edition("Skyrim Special Edition"))
        assertEquals(Variants.edition("Portal 2"), Variants.edition("Portal 2™"))
        assertEquals(Variants.edition("Disco Elysium"), Variants.edition("Disco Elysium - The Final Cut"))
    }

    @Test
    fun `different games keep different edition keys`() {
        assertNotEquals(Variants.edition("Portal"), Variants.edition("Portal 2"))
        assertNotEquals(Variants.edition("Doom"), Variants.edition("Doom Eternal"))
        assertEquals("goldeneye 007", Variants.edition("GoldenEye 007"))
    }

    @Test
    fun `sequels and subtitles share a franchise`() {
        assertEquals("witcher", Variants.franchise("The Witcher 3: Wild Hunt"))
        assertEquals("witcher", Variants.franchise("The Witcher 2: Assassins of Kings Enhanced Edition"))
        assertEquals("portal", Variants.franchise("Portal 2"))
        assertEquals("cyberpunk", Variants.franchise("Cyberpunk 2077"))
        assertEquals("hades", Variants.franchise("Hades II"))
        assertEquals("half life", Variants.franchise("Half-Life 2"))
        assertEquals("grand theft auto", Variants.franchise("Grand Theft Auto V"))
    }
}
