package com.tjcg.habitapp.data

import android.graphics.Bitmap
import com.google.gson.annotations.SerializedName

class HabitPreset {

    @SerializedName("id")
    var id: Int? = null

    @SerializedName("title")
    var title: String? = null

    @SerializedName("description")
    var description: String? = null

    @SerializedName("icon")
    var iconAwesome : String = ""


    var iconImage : Bitmap? = null

    @SerializedName("color")
    var color: String? = null

    @SerializedName("is_active")
    var isActive: Int? = 1

    @SerializedName("habits")
    var habits : List<Habit>? = null

//    var habitsLocal = ArrayList<Habit>()
}