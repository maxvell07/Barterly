package com.example.barterly.utils

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.ImageView
import android.widget.Toast
import androidx.core.net.toUri
import androidx.exifinterface.media.ExifInterface
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStream

object ImageManager {

    const val MAX_IMAGE_SIZE = 1200
    const val WIDTH = 0
    const val HEIGHT = 1

    fun getImageSize(uri:Uri,act:Activity):List<Int>{
        val inStream = act.contentResolver.openInputStream(uri)
        val ftemp = File(act.cacheDir,"temp.tmp")
        if (inStream != null) {
            ftemp.copyInStreamToFile(inStream)
        }

        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true

        }

        BitmapFactory.decodeFile(ftemp.path,options)

        return if (imagerotation(ftemp) == 90){

         listOf(options.outHeight,options.outWidth)

        } else return listOf(options.outWidth,options.outHeight)
    }
    private fun File.copyInStreamToFile(inStream:InputStream){
        this.outputStream().use {
            out -> inStream.copyTo(out)
        }
    }

    private fun imagerotation(imageFile:File):Int{
        val  rotation :Int

        val exif = ExifInterface(imageFile.absolutePath)
        val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,ExifInterface.ORIENTATION_NORMAL)
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90 || orientation == ExifInterface.ORIENTATION_ROTATE_270){

            rotation = 90
        }else{
            rotation = 0
        }
        return rotation
    }
    fun chooseScaleType(im:ImageView,bitmap:Bitmap){

        if (bitmap.width> bitmap.height){
            im.scaleType = ImageView.ScaleType.CENTER_CROP
        } else im.scaleType = ImageView.ScaleType.CENTER_INSIDE
    }

    suspend fun imageResize(uri: ArrayList<Uri>,act: Activity): List<Bitmap> = withContext(Dispatchers.IO){
        val newlist = ArrayList<List<Int>>()
        val bitmaplist = ArrayList<Bitmap>()
        for (n in 0 until uri.size){
            val size = getImageSize( uri[n], act )
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
            bitmaplist.add(Picasso.get().load(uri[n]).resize(newlist[n][WIDTH],newlist[n][HEIGHT]).get())
        }

        }
        return@withContext bitmaplist

    }
}