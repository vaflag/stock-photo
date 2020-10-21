package com.vf.photobank.util

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.app.ActivityCompat
import com.vf.photobank.ui.MainActivity
import java.io.File
import java.io.FileOutputStream

/**
 * Saves an image in device's gallery.
 * The image is provided as a bitmap.
 *
 * @param context
 * @param image the source bitmap.
 * @param fileName the name of the written file.
 * @return a boolean, true if the operation succeeded.
 */
fun saveImageInGallery(context: Context, image: Bitmap, fileName: String): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        saveImageUsingMediaStore(context, image, fileName)
    } else {
        saveImageUsingEnvironment(context, image, fileName)
    }
}

/**
 * Saves an image in device's gallery on SDK version Q or higher.
 *
 * @param context
 * @param bitmap the source bitmap.
 * @param fileName the name of the written file.
 * @return a boolean, true if the operation succeeded.
 */
@SuppressLint("InlinedApi")
private fun saveImageUsingMediaStore(context: Context, bitmap: Bitmap, fileName: String): Boolean {
    val resolver = context.contentResolver
    val contentValues = ContentValues()
    contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
    contentValues.put(MediaStore.MediaColumns.MIME_TYPE, PHOTOS_MIME_TYPE)
    contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/$IMAGES_FOLDER_NAME")
    val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
    val fileOutputStream = imageUri?.let { resolver.openOutputStream(it) }
    val success = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
    fileOutputStream?.close()
    return success
}

/**
 * Saves an image in device's gallery on SDK version P or lower.
 *
 * @param context
 * @param bitmap the source bitmap.
 * @param fileName the name of the written file.
 * @return a boolean, true if the operation succeeded.
 */
private fun saveImageUsingEnvironment(context: Context, bitmap: Bitmap, fileName: String): Boolean {
    @Suppress("DEPRECATION")
    val imagesDir = Environment.getExternalStoragePublicDirectory(
        Environment.DIRECTORY_DCIM
    ).toString() + File.separator + IMAGES_FOLDER_NAME
    val file = File(imagesDir)
    if (!file.exists()) file.mkdirs()
    val imageFile = File(imagesDir, fileName)
    Log.v("FILEPATH", imageFile.absolutePath)
    val fileOutputStream = FileOutputStream(imageFile)
    val success = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
    fileOutputStream.close()
    if (success) {
        // This block adds the photo to phone's gallery
        @Suppress("DEPRECATION")
        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        val contentUri: Uri = Uri.fromFile(imageFile)
        mediaScanIntent.data = contentUri
        context.sendBroadcast(mediaScanIntent)
    }
    return success
}

/**
 * Checks if permission to write to external
 * storage is granted. Asks for it if not.
 *
 * @param activity the running activity.
 */
fun checkStoragePermission(activity: Activity): Boolean {
    val permissionExternalMemory: Int =
        ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    if (permissionExternalMemory != PackageManager.PERMISSION_GRANTED) {
        val storagePermissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        ActivityCompat.requestPermissions(
            activity,
            storagePermissions,
            MainActivity.PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE
        )
        return (permissionExternalMemory == PackageManager.PERMISSION_GRANTED)
    }
    return true
}
