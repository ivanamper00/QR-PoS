package com.qr.pos.amper.utils

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    private val calendar: Calendar get() = Calendar.getInstance()

    fun getCurrentDateString(): String = DateFormat.getDateInstance().format(Date())

    fun getCurrentDateIDFormat(): String {
        val format = SimpleDateFormat("MM_dd_yyyy", Locale.ROOT)
        return format.format(Date())
    }

    fun getConvertedDate(dateMillis: Long, format: String? = "MM/dd/yyyy"): Date {
        val formatter = SimpleDateFormat(format, Locale.ROOT)
        return formatter.parse(formatter.format(Date(dateMillis)))!!
    }

    fun getConvertedDate(dateString: String, format: String? = "MM/dd/yyyy"): Date {
        val formatter = SimpleDateFormat(format, Locale.ROOT)
        return formatter.parse(dateString)!!
    }

    fun datePlus(date: Date, plus: Int, type: Int = Calendar.DAY_OF_MONTH): Date {
        return calendar.apply {
            time = date
            add(type, plus)
        }.time
    }
}