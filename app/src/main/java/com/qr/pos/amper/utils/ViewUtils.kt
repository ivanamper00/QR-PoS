package com.qr.pos.amper.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Environment
import android.view.ViewGroup
import com.qr.pos.amper.inventory.data.dto.Product
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

object ViewUtils {
    fun downloadView(view: ViewGroup, name: String, callback: (Exception?) -> Unit) {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        val file = createDirectory(name)
        if (file.exists()) file.delete()

        try {
            val out = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            out.flush()
            out.close()
            callback(null)
        }catch (e: Exception){
            callback(e)
        }
    }

    private fun createDirectory(filename: String): File {
        val root = "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)}"
        val dir = File(root)
        if(!dir.exists()) dir.mkdir()
        val name = "$filename.jpg"
        return File(dir, name)
    }
}