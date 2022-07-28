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

    @Update()
    fun updateHabitsInCalendar(habitCalendar: HabitCalendar)
}