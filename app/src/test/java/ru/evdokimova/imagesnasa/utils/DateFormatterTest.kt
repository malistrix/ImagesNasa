package ru.evdokimova.imagesnasa.utils

import ru.evdokimova.imagesnasa.utils.DateFormatter.convertDateString
import org.junit.Test
import org.junit.Assert.*


internal class DateFormatterTest {

    @Test
    fun convertDateStringTest() {
        val dateIn = "2011-12-16T19:18:05Z"
        val dateOut = "16 December 2011"
        assertEquals(dateOut, convertDateString(dateIn))
    }
}