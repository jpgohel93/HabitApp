package com.tjcg.habitapp.data

import kotlinx.coroutines.Deferred

interface HabitCalendarInterface {
    fun addInCalendar(habitCalendar : HabitCalendar)
    suspend fun getByCalendarAsync(dateStr: String) : Deferred<HabitCalendar?>
    fun updateHabitsInCalendar(habitCalendar: HabitCalendar)
    suspend fun getFullCalendarAsync() : Deferred<List<HabitCalendar>?>
    fun updateCompletedInCalendar(dateStr: String, completed: Int, callback: (Int) -> Unit)
}