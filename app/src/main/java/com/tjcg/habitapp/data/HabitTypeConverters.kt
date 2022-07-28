package com.tjcg.habitapp.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class HabitTypeConverters {

    @TypeConverter
    fun fromDayHabitsToString(habits : List<HabitInADay>) : String? {
        val gson = Gson()
        val typeToken = object : TypeToken<List<HabitInADay>>() { }
        return gson.toJson(habits, typeToken.type)
    }

    @TypeConverter
    fun toDayHabits(str: String) : List<HabitInADay>? {
        val gson = Gson()
        val typeT = object : TypeToken<List<HabitInADay>>() { }
        return gson.fromJson(str, typeT.type)
    }
}