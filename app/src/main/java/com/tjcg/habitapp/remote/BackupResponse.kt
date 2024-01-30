package com.tjcg.habitapp.remote

import com.google.gson.annotations.SerializedName
import com.tjcg.habitapp.data.HabitPreset

class BackupResponse {

    @SerializedName("status")
    var status: Boolean? = null

    @SerializedName("message")
    var message: String? = null

    @SerializedName("id")
    var id: Int? = null
}