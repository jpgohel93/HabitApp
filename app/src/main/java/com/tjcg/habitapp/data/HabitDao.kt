package com.tjcg.habitapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface HabitDao {

    @Insert
    fun insertNewHabits(habit: Habit)

    @Query("select * from Habits")
    fun getAllHabits() : List<Habit>

    @Query("select * from Habits where id=:id")
    fun getHabitById(id: Int) : Habit?
}