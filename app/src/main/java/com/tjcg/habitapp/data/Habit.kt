package com.tjcg.habitapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Habits")
class Habit(
    var icon: Int,
    var title: String,
    var habitType: Int,
    var repetitionType : Int = Constant.HABIT_REPEAT_AS_WEEKDAY,
    var repetitionDaysArray : String,
    var repetitionDaysCount : Int,
    var repetitionGoalDuration : Int = 0,  // in seconds
    var repetitionGoalCount : Int = 1,
    var doItAtTime : Int = Constant.HABIT_DO_IT_ANYTIME,
    var habitReminderTime : String = "",  // e.g. 14:33, empty means no remainder
    var soundEffect : Int = 0,
    var encouragementText : String = "",
    var endsOnType : Int = Constant.HABIT_END_ON_NULL,
    var endsOnDate : String = "",
    var endsOnDays : Int = -1,

    // for day based calculations
    var repetitionDaysCompleted : Int= 0
) {
    @PrimaryKey(autoGenerate = true)
    var id : Int = 0
}