package com.brett.coloringkids.data

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import kotlin.math.max

class ImageRepository(private val app: Application) {

    /**
     * Decode image from URI with max dimension constraint to prevent OOM
     */
    fun decodeFromUri(uri: Uri, maxDimension: Int = 4096): Bitmap {
        return if (Build.VERSION.SDK_INT >= 28) {
            val source = ImageDecoder.createSource(app.contentResolver, uri)
            ImageDecoder.decodeBitmap(source) { decoder, info, _ ->
                decoder.isMutableRequired = false

                // Calculate scale to fit within max dimension
                val width = info.size.width
                val height = info.size.height
                val maxSize = max(width, height)

                if (maxSize > maxDimension) {
                    val scale = maxDimension.toFloat() / maxSize
                    val targetWidth = (width * scale).toInt()
                    val targetHeight = (height * scale).toInt()
                    decoder.setTargetSize(targetWidth, targetHeight)
                }
            }
        } else {
            // For API < 28, use BitmapFactory with inSampleSize
            app.contentResolver.openInputStream(uri)?.use { inputStream ->
                val options = BitmapFactory.Options().apply {
                    inJustDecodeBounds = true
                }
                BitmapFactory.decodeStream(inputStream, null, options)

                val width = options.outWidth
                val height = options.outHeight
                val maxSize = max(width, height)

                options.inSampleSize = calculateInSampleSize(maxSize, maxDimension)
                options.inJustDecodeBounds = false

                app.contentResolver.openInputStream(uri)?.use { stream2 ->
                    BitmapFactory.decodeStream(stream2, null, options)!!
                }
            } ?: throw IllegalStateException("Cannot open input stream for URI: $uri")
        }
    }

    private fun calculateInSampleSize(maxSize: Int, reqSize: Int): Int {
        var inSampleSize = 1
        if (maxSize > reqSize) {
            val halfSize = maxSize / 2
            while (halfSize / inSampleSize >= reqSize) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }
}
