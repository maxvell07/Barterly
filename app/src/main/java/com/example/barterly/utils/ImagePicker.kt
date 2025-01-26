package com.example.barterly.utils

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.fxn.pix.Options
import com.fxn.pix.Pix

//import io.ak1.pix.models.Mode
//import io.ak1.pix.models.Options


object ImagePiker {
    const val MAX_IMAGE_COUNT = 3
    const val REQuest_CODE_GET_IMAGES = 999

     @SuppressLint("SuspiciousIndentation")
     fun getImages(context: AppCompatActivity, imageCounter:Int) {
        var options = Options.init()
            .setRequestCode(REQuest_CODE_GET_IMAGES)
            .setCount(imageCounter)
            .setFrontfacing(false)
            .setMode(Options.Mode.Picture)
            .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT)
            .setPath("/pix/images")
            Pix.start(context,options)
     }
}