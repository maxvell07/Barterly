package com.example.barterly.act

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.barterly.R
import com.example.barterly.adapters.ImageAdapter
import com.example.barterly.databinding.ActivityEditAdsBinding
import com.example.barterly.dialogs.DialogSpinnerHelper
import com.example.barterly.fragment.FragmentCloseInterface
import com.example.barterly.fragment.ImageListFragment
import com.example.barterly.utils.CityHelper
import com.example.barterly.utils.ImageManager
import com.example.barterly.utils.ImagePiker
import com.fxn.pix.Pix
import com.fxn.utility.PermUtil


class EditAdsAct : AppCompatActivity(), FragmentCloseInterface {
    lateinit var binding: ActivityEditAdsBinding
    private val dialog = DialogSpinnerHelper()
    private var ispermisssionGranted = false
    private lateinit var imageViewAdapter: ImageAdapter
    private var chooseImageFrag: ImageListFragment? = null
    var editimagepos = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditAdsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && requestCode == ImagePiker.REQUEST_CODE_GET_IMAGES) {

            if (data != null) {

                val valueReturn = data.getStringArrayListExtra(Pix.IMAGE_RESULTS)

                if (valueReturn?.size!! > 1 && chooseImageFrag == null) {

                    openChoosenImageFrag(valueReturn)

                } else if (valueReturn.size == 1 && chooseImageFrag == null) {

                  //  imageViewAdapter.update(valueReturn)
                    val  templist = ImageManager.getImageSize(valueReturn[0])

                } else if (chooseImageFrag != null) {

                    chooseImageFrag?.updateAdapter(valueReturn)

                }
            }
        } else if(resultCode == RESULT_OK && requestCode == ImagePiker.REQUEST_CODE_GET_SINGLE_IMAGE){
            if (data != null){
                val uris = data.getStringArrayListExtra(Pix.IMAGE_RESULTS)
                chooseImageFrag?.selectsingleImage(uris?.get(0)!!, editimagepos)
            }
        }
    }
    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PermUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ImagePiker.getImages(this, 3,ImagePiker.REQUEST_CODE_GET_IMAGES)
                } else {
                    Toast.makeText(this, "Approve perm", Toast.LENGTH_LONG)
                }
                return
            }
        }
    }


    private fun init() {

        imageViewAdapter = ImageAdapter()
        binding.vpImages.adapter = imageViewAdapter

    }

    //OnClicks
    fun onClickSelectCountry(view: View) {
        if (binding.selectCity.text != getString(R.string.select_city)) {
            binding.selectCity.text = getString(R.string.select_city)
        }
        val listCountry = CityHelper.getAllCountries(this)
        dialog.showSpinnerDialog(this, listCountry, binding.selectCountry)

    }

    fun onClickSelectCity(view: View) {
        var city = binding.selectCountry.text.toString()
        if (city != view.context.getString(R.string.select_country)) {
            val listCity = CityHelper.getAllCities(this, city)
            dialog.showSpinnerDialog(this, listCity, binding.selectCity)
        } else {
            Toast.makeText(this, "No countryselected", Toast.LENGTH_LONG).show()
        }
    }

    fun onClickGetImages(view: View) {
         if (imageViewAdapter.array.size == 0){

             ImagePiker.getImages(this, 3,ImagePiker.REQUEST_CODE_GET_IMAGES)

         } else {
            openChoosenImageFrag(null)
             chooseImageFrag?.updateAdapterFromEdit(imageViewAdapter.array)
         }
    }

    override fun onFragClose(list: ArrayList<Bitmap>) {
        binding.scrolview.visibility = View.VISIBLE
        imageViewAdapter.update(list)
        chooseImageFrag = null
    }

    private fun openChoosenImageFrag(newlist:ArrayList<String>?){
        chooseImageFrag = ImageListFragment(this, newlist)
        binding.scrolview.visibility = View.GONE
        supportFragmentManager.beginTransaction()
            .replace(R.id.placeholder, chooseImageFrag!!)
            .commit()
    }
}