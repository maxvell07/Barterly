package com.example.barterly.utils

import android.graphics.Bitmap
import android.net.Uri
import android.view.View
import androidx.fragment.app.Fragment
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

    fun pickSeveralImages(context: EditAdsAct, imageCounter: Int) {
        context.addPixToActivity(R.id.placeholder, getOptions(imageCounter)) { result ->
            when (result.status) {
                PixEventCallback.Status.SUCCESS -> {
                    getSeveralSelectedImages(context,result.data)
                }
                    PixEventCallback.Status.BACK_PRESSED -> {}// back pressed called
            }
        }
    }

    fun pickImages(context: EditAdsAct, imageCounter: Int) {
        val f = context.chooseImageFrag
        context.addPixToActivity(R.id.placeholder, getOptions(imageCounter)) { result ->
            when (result.status) {
                PixEventCallback.Status.SUCCESS -> {
                    context.chooseImageFrag = f
                    openChooseImageFrag(context,f!!)
                    context.chooseImageFrag?.updateAdapter(result.data as ArrayList<Uri>,context)
                }
                PixEventCallback.Status.BACK_PRESSED -> {}// back pressed called
            }
        }
    }

    fun pickSingleImages(context: EditAdsAct) {
        val f = context.chooseImageFrag
        context.addPixToActivity(R.id.placeholder, getOptions(1)) { result ->
            when (result.status) {
                PixEventCallback.Status.SUCCESS -> {
                    context.chooseImageFrag = f
                    openChooseImageFrag(context,f!!)
                    singleImage(context,result.data[0])
                }
                PixEventCallback.Status.BACK_PRESSED -> {}// back pressed called
            }
        }
    }
    private fun openChooseImageFrag(context: EditAdsAct, frag:Fragment){
        context.supportFragmentManager.beginTransaction().replace(R.id.placeholder,frag).commit()
    }

    private fun closePixFragment(context: EditAdsAct){
        val flist = context.supportFragmentManager.fragments
        flist.forEach{
            if (it.isVisible) context.supportFragmentManager.beginTransaction().remove(it).commit()
        }
    }

    fun getSeveralSelectedImages(
        context: EditAdsAct,
        uris: List<Uri>
    ) { // добавление картинки(ок) в эдитактивити

        if (uris.size > 1 && context.chooseImageFrag == null) {

            context.openChoosenImageFrag(uris as ArrayList<Uri>)

        } else if (uris.size == 1 && context.chooseImageFrag == null) {

            CoroutineScope(Dispatchers.Main).launch {
                context.binding.vploadbar.visibility = View.VISIBLE
                val bitmaparr =
                    ImageManager.imageResize(uris as ArrayList<Uri>, context) as ArrayList<Bitmap>
                context.binding.vploadbar.visibility = View.GONE
                context.imageViewAdapter.update(bitmaparr)
                closePixFragment(context)
            }

        }
    }

    private fun singleImage(context: EditAdsAct,uri:Uri) { // редактировать выбранную картинку в фрагменте
     context.chooseImageFrag?.selectsingleImage(uri, context.editimagepos)

    }
}