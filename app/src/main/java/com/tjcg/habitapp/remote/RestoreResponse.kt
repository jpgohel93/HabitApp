package com.tjcg.habitapp.remote

import com.google.gson.annotations.SerializedName

class RestoreResponse {

    @SerializedName("status")
    var status: Boolean? = null

    @SerializedName("message")
    var message: String? = null

    @SerializedName("data")
    var data: RestoreData? = null

    class RestoreData {

        @SerializedName("user_id")
        var userId: String? = null

        @SerializedName("json")
        var restoreJson : String? = null

        @SerializedName("created_at")
        var createdAt : String? = null
    }
}