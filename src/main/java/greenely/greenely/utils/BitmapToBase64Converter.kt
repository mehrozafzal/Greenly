package greenely.greenely.utils

import android.graphics.Bitmap
import android.util.Base64
import greenely.greenely.OpenClassOnDebug
import java.io.ByteArrayOutputStream
import javax.inject.Inject

@OpenClassOnDebug
class BitmapToBase64Converter @Inject constructor() {
    fun toBase64(bitmap: Bitmap): String {
        val imageBuffer = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, imageBuffer)
        return Base64.encodeToString(imageBuffer.toByteArray(), Base64.NO_WRAP)
    }
}