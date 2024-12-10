package com.vipul.workmanagerexample

import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {

    @POST("vaccine/patient/patient-vaccine-image")
    fun uploadVaccineDocument(
        @Header("x-access-token") accessToken: String?, @Body body: MultipartBody
    ): Call<ResponseBody>
}