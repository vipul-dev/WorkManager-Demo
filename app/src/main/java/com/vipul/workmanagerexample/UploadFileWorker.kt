package com.vipul.workmanagerexample

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class UploadFileWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {
    private val TAG = "UploadFileWorker" + ""
    override fun doWork(): Result {
        val filePath = inputData.getString("filePath") ?: return Result.failure()

        val file = File(filePath)

        if (!file.exists()) return Result.failure()

        val requestBody = file.asRequestBody("image/jpg".toMediaType())
        val multipart = MultipartBody.Part.createFormData("profileImage", file.name, requestBody)
        val multipartBody =
            MultipartBody.Builder().setType(MultipartBody.FORM).addPart(multipart).build()

        val call = RetrofitInstance.apiService.uploadVaccineDocument(
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MSwiZW1haWwiOiJhbHNoaWZhQHlvcG1haWwuY29tIiwicm9sZSI6ImVsYWJzIiwiaWF0IjoxNzMzMTE1MzM2fQ.TaPSvnAfVbfcr1h-rDADPTNAoU4mSrYVF-fw69u34xI",
            multipartBody
        )

        return try {
            val response = call.execute()
            if (response.isSuccessful) {
                Log.d(TAG, "File upload successfully")
                Result.success()
            } else {
                Log.d(TAG, "Upload failed: ${response.errorBody()?.string()}")
                Result.failure()
            }
        } catch (e: Exception) {
            Log.d(TAG, "Upload failed ${e.localizedMessage}")
            Result.failure()
        }
    }

}