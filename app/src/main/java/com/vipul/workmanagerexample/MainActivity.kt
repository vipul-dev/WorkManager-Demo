package com.vipul.workmanagerexample

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.workDataOf
import com.vipul.workmanagerexample.databinding.ActivityMainBinding
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private var uploadWorkRequest: WorkRequest? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContentView(binding.root)

        try {
            val inputStream = this.assets.open("cat.jpeg")

            val tempFile = File(this.cacheDir, "temp_image.jpg")
            val outputStream = FileOutputStream(tempFile)

            // copy the image content to the temporary file
            val buffer = ByteArray(1024)
            var length: Int

            while (inputStream.read(buffer).also { length = it } != -1) {
                outputStream.write(buffer, 0, length)
            }

            inputStream.close()
            outputStream.close()


            val filePath = tempFile.path


            /**
             * Enqueue a one time request
             * */

            binding.btnOneTimeWorkManager.setOnClickListener {
                uploadWorkRequest =
                    OneTimeWorkRequestBuilder<UploadFileWorker>().setInputData(workDataOf("filePath" to filePath))
                        .setConstraints(
                            Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED)
                                .setRequiresBatteryNotLow(true).build()
                        ).addTag("OneTime").build()
                WorkManager.getInstance(this).enqueue(uploadWorkRequest!!)
            }


            /**
             * Enqueue a PeriodicWorkRequest
             */
            binding.btnPeriodicTimeWorkManager.setOnClickListener {
                uploadWorkRequest =
                    PeriodicWorkRequestBuilder<UploadFileWorker>(15, TimeUnit.MINUTES).setInputData(
                        workDataOf("filePath" to filePath)
                    ).setConstraints(
                        Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED)
                            .setRequiresBatteryNotLow(true).build()
                    ).addTag("Periodic").build()
                WorkManager.getInstance(this).enqueue(uploadWorkRequest!!)
            }


            /**
             * Cancel Periodic work-request
             */
            binding.btnCancelPeriodicTimeWorkManager.setOnClickListener {
                WorkManager.getInstance(this).cancelAllWorkByTag("Periodic")
            }

            if (uploadWorkRequest != null) {

                WorkManager.getInstance(this).getWorkInfoByIdLiveData(uploadWorkRequest?.id!!)
                    .observe(this) {
                        when (it.state) {
                            WorkInfo.State.SUCCEEDED -> Log.d(
                                "UploadFileWorker", "File uploaded successfully."
                            )

                            WorkInfo.State.FAILED -> Log.d(
                                "UploadFileWorker", "File upload failed."
                            )

                            WorkInfo.State.CANCELLED -> Log.d("UploadFileWorker","Cancel ${it.tags} work manager")

                            else -> Log.d("UploadFileWorker", "Work status: ${it.state}")
                        }
                    }

            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}