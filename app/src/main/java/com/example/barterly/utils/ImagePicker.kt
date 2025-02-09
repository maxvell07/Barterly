package com.example.barterly.utils

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import com.example.barterly.act.EditAdsAct
import com.fxn.pix.Options
import com.fxn.pix.Pix
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


object ImagePiker {
    const val MAX_IMAGE_COUNT = 3
    const val REQUEST_CODE_GET_IMAGES = 999
    const val REQUEST_CODE_GET_SINGLE_IMAGE = 888

     @SuppressLint("SuspiciousIndentation")
     fun getImages(context: AppCompatActivity, imageCounter:Int, rCode:Int) {
        var options = Options.init()
            .setRequestCode(rCode)
            .setCount(imageCounter)
            .setFrontfacing(false)
            .setMode(Options.Mode.Picture)
            .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT)
            .setPath("/pix/images")
            Pix.start(context,options)
     }
    fun imagePresenter( resultCode:Int,requestCode:Int,data:Intent?, edact:EditAdsAct){

        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_GET_IMAGES) {

            if (data != null) {

                val valueReturn = data.getStringArrayListExtra(Pix.IMAGE_RESULTS)

                if (valueReturn?.size!! > 1 && edact.chooseImageFrag == null) {

                    edact.openChoosenImageFrag(valueReturn)

                } else if (valueReturn.size == 1 && edact.chooseImageFrag == null) {

                    CoroutineScope(Dispatchers.Main).launch {
                        edact.binding.vploadbar.visibility = View.VISIBLE
                        val bitmaparr = ImageManager.imageResize(valueReturn) as ArrayList<Bitmap>
                        edact.binding.vploadbar.visibility = View.GONE
                        edact.imageViewAdapter.update(bitmaparr)
                    }

                } else if (edact.chooseImageFrag != null) {

                    edact.chooseImageFrag?.updateAdapter(valueReturn)

                }
            }
        } else if(resultCode == RESULT_OK && requestCode == REQUEST_CODE_GET_SINGLE_IMAGE){
            if (data != null){
                val uris = data.getStringArrayListExtra(Pix.IMAGE_RESULTS)
                edact.chooseImageFrag?.selectsingleImage(uris?.get(0)!!, edact.editimagepos)
            }
        }

    }
}