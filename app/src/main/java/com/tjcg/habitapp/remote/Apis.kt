package com.tjcg.habitapp.remote

import com.tjcg.habitapp.data.HabitPreset
import retrofit2.Call
import retrofit2.http.*

interface Apis {

    @POST("register")
    @FormUrlEncoded
    fun registerUser(@Field("name") name: String,
                    @Field("email") email: String,
                    @Field("password") password: String,
                    @Field("device_token") deviceToken: String) : Call<RegisterResponse>

    @POST("login")
    @FormUrlEncoded
    fun loginUser(@Field("email") email: String,
                @Field("password") password: String) : Call<RegisterResponse>

    @GET("presets")
    fun getHabitPresets(@Header("Authorization") authToken: String) : Call<PresetResponse>
}