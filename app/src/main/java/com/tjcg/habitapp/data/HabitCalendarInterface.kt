package com.tjcg.habitapp.data

interface HabitCalendarInterface {
    fun addInCalendar(habitCalendar : HabitCalendar)
    fun getByCalendar(dateStr: String, callback: (HabitCalendar?) -> Unit)
    fun updateHabitsInCalendar(habitCalendar: HabitCalendar)
}