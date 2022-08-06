package com.tjcg.habitapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import androidx.room.Update

@Dao
interface HabitCalendarDao {

    @Insert(onConflict = REPLACE)
    fun insertInCalendar(habitCalendar : HabitCalendar)

    @Query("select * from HabitCalendar where calendarDate=:dateStr")
    fun getHabitsByDate(dateStr : String) : HabitCalendar?

    @Query("select * from HabitCalendar")
    fun getFullCalendar() : List<HabitCalendar>?

    @Update()
    fun updateHabitsInCalendar(habitCalendar: HabitCalendar)

    @Query("update HabitCalendar set completed=:completed where calendarDate=:dateStr")
    fun updateCompletionRate(dateStr: String, completed: Int) : Int
}