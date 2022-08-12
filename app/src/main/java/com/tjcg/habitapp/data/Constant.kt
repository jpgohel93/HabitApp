package com.tjcg.habitapp.data

import android.content.Context
import android.content.Context.DISPLAY_SERVICE
import android.content.Context.WINDOW_SERVICE
import android.graphics.Point
import android.hardware.display.DisplayManager
import android.os.Build
import android.util.Log
import android.view.WindowManager
import androidx.core.content.ContextCompat.getSystemService
import com.tjcg.habitapp.MainActivity
import java.util.*

object Constant {

    const val PAGE_1 = 1
    const val PAGE_2 = 2
    const val PAGE_3 = 3
    const val PAGE_4 = 4

    const val HABIT_REPEAT_AS_WEEKDAY = 1
    const val HABIT_REPEAT_IN_WEEK = 2
    const val HABIT_REPEAT_IN_MONTH = 3
    const val HABIT_REPEAT_IN_YEAR = 4

    const val HABIT_DO_IT_ANYTIME = 0
    const val HABIT_DO_IT_MORNING = 1
    const val HABIT_DO_IT_AFTERNOON = 2
    const val HABIT_DO_IT_EVENING = 3

    const val HABIT_END_ON_NULL = 0
    const val HABIT_END_ON_DATE = 1
    const val HABIT_END_ON_DAYS = 2

    const val SUNDAY = 1
    const val MONDAY = 2
    const val TUESDAY = 3
    const val WEDNESDAY = 4
    const val THURSDAY = 5
    const val FRIDAY = 6
    const val SATURDAY = 7

    const val CURRENT_TIME_MORNING = 1
    const val CURRENT_TIME_AFTERNOON = 2
    const val CURRENT_TIME_EVENING = 3

    const val BAR_WIDTH_360 = 30
    const val BAR_WIDTH_410 = 50

    var dateToday = -1
    var monthToday = -1
    var todayString : String= ""

    var notificationDataFile = "NOTIFICATION_SETTINGS"

    fun generateDateString(cal : Calendar) : String {
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DAY_OF_MONTH)
        return "$year-$month-$day"
    }

    fun generateYearCalendar() : ArrayList<ArrayList<WeekCalendarRow>> {
        val cal = Calendar.getInstance()
        val yearNow = cal.get(Calendar.YEAR)
        //set calendar to first day of year
        cal.set(Calendar.WEEK_OF_YEAR, 1)
        val thatWeekday = cal.get(Calendar.DAY_OF_WEEK)
        cal.add(Calendar.DAY_OF_MONTH, 1 - thatWeekday)
        Log.d("Firstdat", "${cal.get(Calendar.DAY_OF_MONTH)}, ${cal.get(Calendar.MONTH)}, ${cal.get(Calendar.WEEK_OF_YEAR)}" +
                ", $thatWeekday")
        var yearAfter = 0
        val wholeCalendar = ArrayList<ArrayList<WeekCalendarRow>>()
        do {
            val myCalendars = ArrayList<WeekCalendarRow>()
            for (i in 1..7) {
                val date = cal.get(Calendar.DAY_OF_MONTH)
                val month = cal.get(Calendar.MONTH)
                myCalendars.add(WeekCalendarRow(date, i, month))
                cal.add(Calendar.DAY_OF_MONTH, 1)
            }
            Log.d("Week", myCalendars.joinToString())
            wholeCalendar.add(myCalendars)
            yearAfter = cal.get(Calendar.YEAR)
        } while (yearAfter == yearNow)

        return wholeCalendar
    }

    fun getDisplayWidth(ctx: Context) : Int {
        var displayWidth = 0
        var displayHeight = 0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val winManager = (ctx as MainActivity).getSystemService(WINDOW_SERVICE) as WindowManager
            val winMetrics = winManager.currentWindowMetrics
            val bounds = winMetrics.bounds
            displayWidth = bounds.width()
            displayHeight = bounds.height()
        } else {
            val displayManager = (ctx as MainActivity).getSystemService(DISPLAY_SERVICE) as DisplayManager
            val display = displayManager.getDisplay(0)
            val point = Point()
            display.getSize(point)
            displayWidth = point.x
            displayHeight = point.y
        }
        return displayWidth
    }

    fun getTodayArray() : Array<Int> {
        if (dateToday <= 0 || monthToday <= 0) {
            val cal = Calendar.getInstance()
            dateToday = cal.get(Calendar.DAY_OF_MONTH)
            monthToday = cal.get(Calendar.MONTH)
        }
        return arrayOf(dateToday, monthToday)
    }

    fun provideShotDay(day: Int) : String {
        return when(day) {
            1 -> "SUN"
            2 -> "MON"
            3 -> "TUE"
            4 -> "WED"
            5 -> "THU"
            6 -> "FRI"
            7 -> "SAT"
            else -> "ERR"
        }
    }

    fun provideDaysInTwoLetters(day: Int) :String {
        return when(day) {
            1 -> "Su"
            2 -> "Mo"
            3 -> "Tu"
            4 -> "We"
            5 -> "Th"
            6 -> "Fr"
            7 -> "Sa"
            else -> "Er"
        }
    }

    fun convertSecondsToText(secs: Int, talkable: Boolean = false) : String {
        val hour = secs / 3600
        val min = (secs % 3600) / 60
        val sec = (secs % 3600) % 60
        val minStr = if (min < 10) "0$min" else "$min"
        val secStr = if (sec < 10) "0$sec" else "$sec"
        return if (talkable) {
            if (hour == 0) {
                "$min Minutes"
            } else {
                "$hour Hour $min Minutes"
            }
        } else {
            if (hour == 0) {
                "$minStr:$secStr"
            } else {
                "$hour:$minStr:$secStr"
            }
        }
    }

    fun getCurrentTimePeriod() : Int {
        val cal = Calendar.getInstance()
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        return if (hour < 12) {
            CURRENT_TIME_MORNING
        } else if (hour < 17) {
            CURRENT_TIME_AFTERNOON
        } else {
            CURRENT_TIME_EVENING
        }
    }

}