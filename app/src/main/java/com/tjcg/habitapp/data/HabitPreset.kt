package com.tjcg.habitapp.data

import android.graphics.Bitmap

class HabitPreset(val title:String) {
    var iconAwesome : String = ""
    var iconImage : Bitmap? = null
    var habits = ArrayList<Habit>()
}