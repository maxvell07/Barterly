package com.example.barterly.utils

import android.content.Intent
import android.graphics.Bitmap
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import com.example.barterly.R
import com.example.barterly.act.EditAdsAct
import io.ak1.pix.helpers.PixEventCallback
import io.ak1.pix.helpers.addPixToActivity
import io.ak1.pix.models.Mode
import io.ak1.pix.models.Options
import io.ak1.pix.models.Ratio
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


object ImagePiker {
    const val MAX_IMAGE_COUNT = 3

    fun getOptions(imageCounter: Int): Options {
        val options = Options().apply{
            ratio = Ratio.RATIO_AUTO                                    //Image/video capture ratio
            count = imageCounter                                                   //Number of images to restrict selection count             //Number for columns in grid
            path = "/pix/images"                                         //Custom Path For media Storage
            isFrontFacing = false                                       //Front Facing camera on start
            mode = Mode.Picture                                             //Option to select only pictures or videos or both
        }
    return options
    }

    fun launcher(
        context: EditAdsAct,
        launcher: ActivityResultLauncher<Intent>?,
        imageCounter: Int
    ) {
        context.addPixToActivity(R.id.placeholder, getOptions(imageCounter)) { result ->
            when (result.status) {
                PixEventCallback.Status.SUCCESS -> {
                    val flist = context.supportFragmentManager.fragments
                    flist.forEach{
                        if (it.isVisible) context.supportFragmentManager.beginTransaction().remove(it).commit()
                    }
                }
                    PixEventCallback.Status.BACK_PRESSED -> {}// back pressed called
            }
        }
    }

    fun getLauncherForSeveralImages(context: EditAdsAct): ActivityResultLauncher<Intent> { // добавление картинки(ок) в эдитактивити
//
        return context.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
//            result: ActivityResult ->
//            if (result.resultCode == RESULT_OK) {
        }
//
//                if (result.data != null) {
//
//                    val valueReturn = result.data?.getStringArrayListExtra(Pix.IMAGE_RESULTS)
//
//                    if (valueReturn?.size!! > 1 && context.chooseImageFrag == null) {
//
//                        context.openChoosenImageFrag(valueReturn)
//
//                    } else if (valueReturn.size == 1 && context.chooseImageFrag == null) {
//
//                        CoroutineScope(Dispatchers.Main).launch {
//                            context.binding.vploadbar.visibility = View.VISIBLE
//                            val bitmaparr =
//                                ImageManager.imageResize(valueReturn) as ArrayList<Bitmap>
//                            context.binding.vploadbar.visibility = View.GONE
//                            context.imageViewAdapter.update(bitmaparr)
//                        }
//
//                    } else if (context.chooseImageFrag != null) {
//
//                        context.chooseImageFrag?.updateAdapter(valueReturn)
//
//                    }
//                }
//            }
//        }
    }

    fun getLauncherForSingleImage(context: EditAdsAct): ActivityResultLauncher<Intent> { // редактировать выбранную картинку в фрагменте
        return context.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result: ActivityResult ->
//            if (result.resultCode == RESULT_OK) {
        }
//                if (result.data != null) {
//                    val uris = result.data?.getStringArrayListExtra(Pix.IMAGE_RESULTS)
//                    context.chooseImageFrag?.selectsingleImage(uris?.get(0)!!, context.editimagepos)
//                }
//            }
//        }
    }
}