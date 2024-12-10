package com.vipul.workmanagerexample

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private const val BASE_URL = "http://api.recordified.com:3004/api/al_shifa/"

    val apiService: ApiService by lazy {
        Retrofit.Builder().baseUrl(BASE_URL).client(OkHttpClient())
            .addConverterFactory(GsonConverterFactory.create()).build()
            .create(ApiService::class.java)
    }

}