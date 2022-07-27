package ru.evdokimova.imagesnasa.data.workers

import android.app.PendingIntent
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.FileProvider
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import ru.evdokimova.imagesnasa.utils.Constants
import ru.evdokimova.imagesnasa.utils.MyChannels
import ru.evdokimova.imagesnasa.utils.makeStatusNotification
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream


private const val TAG = "DownloadImageWorker "

/**
 * Получает изображение по href и сохраняет его, уведомляет о процессе
 */
class DownloadImageWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {

    private var imageHref = ""

    override fun doWork(): Result {
        makeNotification("image download in progress")
        imageHref = inputData.getString("HREF") ?: ""
        return try {
            Glide.with(applicationContext)
                .asBitmap()
                .load(imageHref)
                .listener(requestListener).submit()
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "${e.message} ${Log.getStackTraceString(e)}")
            Result.failure()
        }
    }

    private val requestListener = object : RequestListener<Bitmap> {
        override fun onLoadFailed(
            e: GlideException?,
            model: Any?,
            target: Target<Bitmap>?,
            isFirstResource: Boolean
        ): Boolean {
            Log.e(TAG, "image not saved ${e?.stackTrace}")
            makeNotification("image not saved Error Glide ${e?.message}")
            return true
        }

        override fun onResourceReady(
            resource: Bitmap?, model: Any?, target: Target<Bitmap>?,
            dataSource: DataSource?, isFirstResource: Boolean
        ): Boolean {
            try {
                val imgName = imageHref.split("/").last()
                val uri = saveImageInFolder(resource!!, Constants.PICTURES_FOLDERS_NAME, imgName)
                makeNotification("image saved", getImagePaddingIntent(uri))
            } catch (e: Exception) {
                Log.e(TAG, "${e.message} ${Log.getStackTraceString(e)}")
                makeNotification("image not saved Error: ${e.message}")
            }
            return true
        }
    }

    /**
     * return Uri saved image
     */
    private fun saveImageInFolder(bitmap: Bitmap, folder: String, name: String): Uri {
        var imageUri: Uri? = null
        val appContext = applicationContext
        val contentResolver: ContentResolver = appContext.contentResolver
        val fileOutputStream: OutputStream? =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = ContentValues()
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "$name.jpg")
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                val picturesFolder = Environment.DIRECTORY_PICTURES + "/$folder/"
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, picturesFolder)
                imageUri = contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    contentValues
                )
                val uri = imageUri
                uri?.let {
                    contentResolver.openOutputStream(uri)
                }
            } else {
                val pictureDirectory = Environment.DIRECTORY_PICTURES
                val imagesDir: String =
                    Environment.getExternalStoragePublicDirectory(pictureDirectory).toString()
                val picturesFolder = File(imagesDir, folder)
                if (!picturesFolder.exists()) {
                    picturesFolder.mkdir()
                }
                val image = File(picturesFolder.toString(), name)
                imageUri = FileProvider.getUriForFile(
                    appContext, appContext.packageName + ".provider", image
                )

                FileOutputStream(image)
            }
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
        fileOutputStream?.close()
        return imageUri!!
    }

    /**
     * return PendingIntent для открытия изображения в другом приложении
     */
    private fun getImagePaddingIntent(uri: Uri): PendingIntent {
        val intent = Intent()
        intent.action = Intent.ACTION_VIEW
        intent.setDataAndType(uri, "image/*")
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        return PendingIntent
            .getActivity(applicationContext, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    }

    private fun makeNotification(message: String, pendingIntent: PendingIntent? = null) {
        val appContext = applicationContext
        makeStatusNotification(
            message,
            appContext,
            MyChannels.SAVE_IMAGE,
            1,
            "Image download",
            pendingIntent
        )
    }
}