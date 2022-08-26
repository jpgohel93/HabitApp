package com.tjcg.habitapp.remote

import com.google.gson.annotations.SerializedName

class RegisterResponse {

    @SerializedName("status")
    var status: Boolean? = null

    @SerializedName("message")
    var message: String? = null

    @SerializedName("token")
    var token: String? = null

    @SerializedName("data")
    var data: RegisterData? = null

    class RegisterData {

        @SerializedName("name")
        var name: String? = null
    }
}