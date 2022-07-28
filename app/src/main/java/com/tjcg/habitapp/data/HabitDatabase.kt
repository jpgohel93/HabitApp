package com.tjcg.habitapp.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@TypeConverters(HabitTypeConverters::class)
@Database(entities = [Habit::class, HabitCalendar::class], version = 1)
abstract class HabitDatabase : RoomDatabase() {
    abstract fun habitDao() : HabitDao
    abstract fun habitCalendarDao() : HabitCalendarDao
}