package com.example.barterly.utils

import androidx.appcompat.app.AppCompatActivity
import com.fxn.pix.Options
import com.fxn.pix.Pix

//import io.ak1.pix.models.Mode
//import io.ak1.pix.models.Options


object ImagePiker {
    const val MAX_IMAGE_COUNT = 3
    const val REQUEST_CODE_GET_IMAGES = 999
    const val REQUEST_CODE_GET_SINGLE_IMAGE = 888

     fun getImages(context: AppCompatActivity, imageCounter:Int,rCode:Int) {
        var options = Options.init()
            .setRequestCode(rCode)
            .setCount(imageCounter)
            .setFrontfacing(false)
            .setMode(Options.Mode.Picture)
            .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT)
            .setPath("/pix/images")
            Pix.start(context,options)
     }
}