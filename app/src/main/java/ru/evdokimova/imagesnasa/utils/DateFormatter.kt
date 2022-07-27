package ru.evdokimova.imagesnasa.utils

import java.text.SimpleDateFormat
import java.util.*

object DateFormatter {

    private fun stringToDate(date: String): Date {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH)
        return dateFormat.parse(date) ?: Date()
    }

    fun convertDateString(dateString: String): String {
        val date = stringToDate(dateString)
        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH)
        return dateFormat.format(date)
    }
}