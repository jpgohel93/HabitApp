package com.tjcg.habitapp.data

import kotlinx.coroutines.Deferred

interface HabitDatabaseInterface {
    fun addHabit(habit: Habit)
    suspend fun getAllHabitsAsync() : Deferred<List<Habit>>
    suspend fun getHabitByIdAsync(id: Int) : Deferred<Habit?>
}