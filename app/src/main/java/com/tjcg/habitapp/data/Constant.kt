package com.tjcg.habitapp.data

import android.content.Context
import android.content.Context.DISPLAY_SERVICE
import android.content.Context.WINDOW_SERVICE
import android.graphics.Point
import android.hardware.display.DisplayManager
import android.os.Build
import android.util.Log
import android.view.WindowManager
import com.tjcg.habitapp.MainActivity
import java.util.*

object Constant {

    const val PAGE_1 = 1
    const val PAGE_2 = 2
    const val PAGE_3 = 3
    const val PAGE_4 = 4
    const val PAGE_IN = 5

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

    const val PRESET_REGULAR = 0
    const val PRESET_NEGATIVE = 1
    const val PRESET_ONE_TIME = 2

    const val BAR_WIDTH_360 = 30
    const val BAR_WIDTH_410 = 50

    const val UUID_SEPARATOR = " ; "

    var dateToday = -1
    private var monthToday = -1
    var todayString : String= ""

    var notificationDataFile = "NOTIFICATION_SETTINGS"
    var timePeriodDataFile = "TIME_PERIOD_DATA"

    // for remote server
    const val BASE_URL = "https://habit.tjcg.in/api/"
    const val CONNECTION_TIMEOUT = 30L
    const val READ_TIMEOUT = 30L
    const val WRITE_TIMEOUT = 30L
    const val HABIT_BACKUP_JSON = "habits"
    const val CALENDAR_BACKUP_JSON = "calendars"
    var authorizationToken = ""
    var habitAndCalendarBackupSeparator = "---"


    // shared preferences
    const val PREFS_APP = "app_preferences"
    const val PREFS_AUTHORIZATION = "auth_token"
    const val PREFS_GLOBAL_NOTIFICATION_IDS = "globalNotificationUUIds"
    const val PREFS_ASKED_AUTORUN = "autoRunAsked"


    //notification messages
    const val GLOBAL_NOTI_TITLE = "Habit global notification"
    const val GLOBAL_NOTI_SUB = "Habit global Subtitle"
    const val MORNING_NOTI_TITLE = "Habit morning notification"
    const val MORNING_NOTI_SUB = "Habit morning Subtitle"
    const val AFTERNOON_NOTI_TITLE = "Habit afternoon notification"
    const val AFTERNOON_NOTI_SUB = "Habit afternoon Subtitle"
    const val EVENING_NOTI_TITLE = "Habit evening notification"
    const val EVENING_NOTI_SUB = "Habit evening Subtitle"


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
        Log.d("FirstDate", "${cal.get(Calendar.DAY_OF_MONTH)}, ${cal.get(Calendar.MONTH)}, ${cal.get(Calendar.WEEK_OF_YEAR)}" +
                ", $thatWeekday")
        var yearAfter : Int
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
        //     var displayHeight = 0
        val displayWidth : Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val winManager = (ctx as MainActivity).getSystemService(WINDOW_SERVICE) as WindowManager
            val winMetrics = winManager.currentWindowMetrics
            val bounds = winMetrics.bounds
            bounds.width()
            //       displayHeight = bounds.height()
        } else {
            val displayManager = (ctx as MainActivity).getSystemService(DISPLAY_SERVICE) as DisplayManager
            val display = displayManager.getDisplay(0)
            val point = Point()
            display.getSize(point)
            point.x
            //        displayHeight = point.y
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

    fun convertTimeDigitToText(time: Int) : String {
        val timeStr = time.toString()
        return if (timeStr.length == 1) {
            "0$time"
        } else {
            timeStr
        }
    }

    fun getCurrentTimePeriod(timePeriodData: TimePeriodData) : Int {
        val cal = Calendar.getInstance()
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        val minute = cal.get(Calendar.MINUTE)
        var period = CURRENT_TIME_MORNING
        if (hour == timePeriodData.timePeriodAfternoon[0]) {
            period = if(minute >= timePeriodData.timePeriodAfternoon[1]) {
               CURRENT_TIME_AFTERNOON
            } else {
                CURRENT_TIME_MORNING
            }
            return period
        }
        if (hour == timePeriodData.timePeriodEvening[0]) {
            period = if ( minute >= timePeriodData.timePeriodEvening[1]) {
                CURRENT_TIME_EVENING
            } else {
                CURRENT_TIME_AFTERNOON
            }
            return period
        }
        if (hour == timePeriodData.timePeriodEnd[0]) {
            period = if (minute < timePeriodData.timePeriodEnd[1]) {
                CURRENT_TIME_EVENING
            } else {
                CURRENT_TIME_MORNING
            }
        }
        if (hour > timePeriodData.timePeriodAfternoon[0] &&
                hour < timePeriodData.timePeriodEvening[0]) {
            return CURRENT_TIME_AFTERNOON
        }
        if (hour > timePeriodData.timePeriodEvening[0] &&
                hour < timePeriodData.timePeriodEnd[0]) {
            return CURRENT_TIME_EVENING
        }
        return period
    }

}