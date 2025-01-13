package com.example.barterly.utils

import androidx.appcompat.app.AppCompatActivity
import io.ak1.pix.models.Mode
import io.ak1.pix.models.Options


object ImagePiker {
    const val MAX_IMAGE_COUNT = 5
     fun getOptions(imageCounter: Int): Options {
        return Options().apply {
            count = imageCounter
            isFrontFacing = false
            mode = Mode.Picture
            ratio
            path = "/pix/images"
        }
    }
}