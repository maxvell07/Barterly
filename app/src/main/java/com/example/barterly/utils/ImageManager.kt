package com.example.barterly.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.exifinterface.media.ExifInterface
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.File

object ImageManager {

    const val MAX_IMAGE_SIZE = 1200
    const val WIDTH = 0
    const val HEIGHT = 1

    fun getImageSize(uri:String):List<Int>{


        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true

        }

        BitmapFactory.decodeFile(uri,options)

        return if (imagerotation(uri) == 90){

         listOf(options.outHeight,options.outWidth)

        } else return listOf(options.outWidth,options.outHeight)
    }

    private fun imagerotation(uri:String):Int{
        val  rotation :Int
        val imageFile = File(uri)
        val exif = ExifInterface(imageFile.absolutePath)
        val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,ExifInterface.ORIENTATION_NORMAL)
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90 || orientation == ExifInterface.ORIENTATION_ROTATE_270){

            rotation = 90
        }else{
            rotation = 0
        }
        return rotation
    }

    suspend fun imageResize(uri: List<String>): List<Bitmap> = withContext(Dispatchers.IO){
        val newlist = ArrayList<List<Int>>()
        val bitmaplist = ArrayList<Bitmap>()
        for (n in 0 until uri.size){
            val size = getImageSize( uri[n])
            val imageratio =  size[WIDTH].toFloat() / size[HEIGHT].toFloat()

            if (imageratio > 1){

                if (size[WIDTH] > MAX_IMAGE_SIZE ){
                    newlist.add(listOf(MAX_IMAGE_SIZE, (MAX_IMAGE_SIZE/ imageratio).toInt()))
                }else {
                    newlist.add(listOf(size[WIDTH],size[HEIGHT]))
                }

            } else {
                if (size[HEIGHT] > MAX_IMAGE_SIZE ){
                    newlist.add(listOf((MAX_IMAGE_SIZE* imageratio).toInt(),MAX_IMAGE_SIZE))
                }else {
                    newlist.add(listOf(size[WIDTH],size[HEIGHT]))
                }

            }
        }
        for (n in 0 until uri.size) {

            val e = kotlin.runCatching {
            bitmaplist.add(Picasso.get().load(File(uri[n])).resize(newlist[n][WIDTH],newlist[n][HEIGHT]).get())
        }

        }
        return@withContext bitmaplist

    }
}