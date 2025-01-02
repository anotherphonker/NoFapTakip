package com.resulbey.nofaptakip

import android.graphics.drawable.ColorDrawable
import android.widget.CalendarView
import androidx.core.content.ContextCompat
import java.util.*

class DayDecorator(
    private val calendarView: CalendarView,
    private val startDate: Long,
    private val relapseDates: Set<String>
) {
    fun decorate() {
        try {
            val calendar = Calendar.getInstance()
            val dayView = calendarView.javaClass.getDeclaredField("mDayNamesHeader")
            dayView.isAccessible = true
            val dayViewInstance = dayView.get(calendarView)
            
            val cells = dayViewInstance.javaClass.getDeclaredField("mDayCells")
            cells.isAccessible = true
            val cellsArray = cells.get(dayViewInstance) as Array<*>
            
            for (i in cellsArray.indices) {
                val cell = cellsArray[i]
                if (cell != null) {
                    calendar.set(Calendar.DAY_OF_MONTH, i + 1)
                    val date = calendar.timeInMillis
                    
                    val color = when {
                        date < startDate -> ContextCompat.getColor(calendarView.context, android.R.color.darker_gray)
                        relapseDates.contains(formatDate(date)) -> ContextCompat.getColor(calendarView.context, android.R.color.holo_red_light)
                        else -> ContextCompat.getColor(calendarView.context, android.R.color.holo_green_light)
                    }
                    
                    val background = cell.javaClass.getDeclaredField("background")
                    background.isAccessible = true
                    background.set(cell, ColorDrawable(color))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun formatDate(date: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = date
        return String.format(
            Locale.getDefault(),
            "%04d-%02d-%02d",
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH) + 1,
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }
} 