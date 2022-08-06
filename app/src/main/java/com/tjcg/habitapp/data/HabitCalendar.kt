package com.tjcg.habitapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "HabitCalendar")
class HabitCalendar(
    @PrimaryKey var calendarDate: String = "",
    var weekdayInt: Int ,
    var habitsInADay : List<HabitInADay>,
    var completed : Int = 0
)