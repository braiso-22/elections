package com.braiso_22.elections.common

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FilterHelpersTest {

    @Test
    fun matchesWhenValueContainsTheQuery() {
        assertTrue("test".containsSegment("Partido test"))
    }

    @Test
    fun ignoresCase() {
        assertTrue("PARTIDO".containsSegment("Partido test"))
        assertTrue("pt".containsSegment("PT"))
    }

    @Test
    fun ignoresCaseOnAccentedCharacters() {
        assertTrue("ESPAÑA".containsSegment("https://www.gobiernodeespaña.com/"))
    }

    @Test
    fun doesNotMatchWhenValueDoesNotContainTheQuery() {
        assertFalse("futuro".containsSegment("Partido test"))
    }

    @Test
    fun emptyQueryNeverMatches() {
        assertFalse("".containsSegment("Partido test"))
    }

    @Test
    fun emptyValueNeverMatches() {
        assertFalse("test".containsSegment(""))
    }
}
