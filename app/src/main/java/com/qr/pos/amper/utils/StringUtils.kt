package com.qr.pos.amper.utils

import android.graphics.Bitmap
import android.graphics.Color
import android.util.Patterns
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import java.text.SimpleDateFormat
import java.util.*

object StringUtils {
    fun getDomain(email: String) : String{
        return if(Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            email.split("@").last()
        } else ""
    }

    fun generateBitmap(productCode: String?): Bitmap? {
        val result: BitMatrix
        try {
            result = MultiFormatWriter().encode(productCode, BarcodeFormat.QR_CODE, 300, 300, null)
        }catch (e: Exception){
            e.printStackTrace()
            return null
        }
        val width = result?.width ?: 0
        val height = result?.height ?: 0
        val pixels = IntArray(width * height)
        for(i in 0 until height){
            val offset = i * width
            for(j in 0 until width){
                pixels[offset + j] = if(result?.get(j,i) == true) Color.BLACK else Color.WHITE
            }
        }
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bitmap.setPixels(pixels, 0,width,0,0,width,height)
        return bitmap
    }

    fun dateToString(format: String){
        val dateFormat = SimpleDateFormat("", Locale.ROOT)
    }
}