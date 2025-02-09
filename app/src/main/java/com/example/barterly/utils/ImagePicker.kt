package com.example.barterly.utils

import android.content.Intent
import android.graphics.Bitmap
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import com.example.barterly.act.EditAdsAct
import com.fxn.pix.Options
import com.fxn.pix.Pix
import com.fxn.utility.PermUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


object ImagePiker {
    const val MAX_IMAGE_COUNT = 3
    const val REQUEST_CODE_GET_IMAGES = 999
    const val REQUEST_CODE_GET_SINGLE_IMAGE = 888

    fun getOptions(imageCounter: Int): Options {
        var options = Options.init()
            .setCount(imageCounter)
            .setFrontfacing(false)
            .setMode(Options.Mode.Picture)
            .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT)
            .setPath("/pix/images")
        return options
    }

    fun launcher(
        context: EditAdsAct,
        launcher: ActivityResultLauncher<Intent>?,
        imageCounter: Int
    ) {
        PermUtil.checkForCamaraWritePermissions(context, null) {
            val intent = Intent(context, Pix::class.java).apply {
                putExtra("options", getOptions(imageCounter))
            }
            launcher?.launch(intent)
        }
    }

    fun getLauncherForSeveralImages(context: EditAdsAct): ActivityResultLauncher<Intent> { // добавление картинки(ок) в эдитактивити

        return context.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {

                if (result.data != null) {

                    val valueReturn = result.data?.getStringArrayListExtra(Pix.IMAGE_RESULTS)

                    if (valueReturn?.size!! > 1 && context.chooseImageFrag == null) {

                        context.openChoosenImageFrag(valueReturn)

                    } else if (valueReturn.size == 1 && context.chooseImageFrag == null) {

                        CoroutineScope(Dispatchers.Main).launch {
                            context.binding.vploadbar.visibility = View.VISIBLE
                            val bitmaparr =
                                ImageManager.imageResize(valueReturn) as ArrayList<Bitmap>
                            context.binding.vploadbar.visibility = View.GONE
                            context.imageViewAdapter.update(bitmaparr)
                        }

                    } else if (context.chooseImageFrag != null) {

                        context.chooseImageFrag?.updateAdapter(valueReturn)

                    }
                }
            }
        }
    }

    fun getLauncherForSingleImage(context: EditAdsAct): ActivityResultLauncher<Intent> { // редактировать выбранную картинку в фрагменте
        return context.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                if (result.data != null) {
                    val uris = result.data?.getStringArrayListExtra(Pix.IMAGE_RESULTS)
                    context.chooseImageFrag?.selectsingleImage(uris?.get(0)!!, context.editimagepos)
                }
            }
        }
    }
}