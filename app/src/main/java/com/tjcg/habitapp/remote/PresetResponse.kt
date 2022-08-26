package com.tjcg.habitapp.remote

import com.google.gson.annotations.SerializedName
import com.tjcg.habitapp.data.HabitPreset

class PresetResponse {

    @SerializedName("status")
    var status: Boolean? = null

    @SerializedName("message")
    var message: String? = null

    @SerializedName("data")
    var data: List<HabitPreset>?  = null
}