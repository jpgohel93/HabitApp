package com.tjcg.habitapp.remote

import com.tjcg.habitapp.data.Constant
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

class ApiService {

    companion object {
        private val client = okhttp3.OkHttpClient.Builder()
            .connectTimeout(Constant.CONNECTION_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(Constant.READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(Constant.WRITE_TIMEOUT, TimeUnit.SECONDS)
            .retryOnConnectionFailure(false)
         /*   .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer ${Constant.authorizationToken}").build()
                chain.proceed(request)
            }  */
            .build()


        private val retrofitBuilder = Retrofit.Builder()
            .baseUrl(Constant.BASE_URL)
            .client(client)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())

        private val retrofit = retrofitBuilder.build()
        val apiService: Apis? = retrofit.create(Apis::class.java)
    }
}