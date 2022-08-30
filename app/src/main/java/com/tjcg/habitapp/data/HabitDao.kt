package com.tjcg.habitapp.data

import androidx.room.*

@Dao
interface HabitDao {

    @Insert
    fun insertNewHabits(habit: Habit)

    @Insert
    fun insertAllHabits(allHabits : List<Habit>)

    @Query("select * from Habits")
    fun getAllHabits() : List<Habit>

    @Query("select * from Habits where id=:id")
    fun getHabitById(id: Int) : Habit?

    @Update
    fun updateHabit(habit: Habit)

    @Query("delete from Habits")
    fun deleteAllHabits()
}