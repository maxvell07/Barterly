package com.example.barterly.act

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import com.example.barterly.R
import com.example.barterly.adapters.ImageAdapter
import com.example.barterly.data.Offer
import com.example.barterly.database.DbManager
import com.example.barterly.databinding.ActivityEditAdsBinding
import com.example.barterly.dialogs.DialogSpinnerHelper
import com.example.barterly.fragment.FragmentCloseInterface
import com.example.barterly.fragment.ImageListFragment
import com.example.barterly.utils.CityHelper
import com.example.barterly.utils.ImagePiker
import com.fxn.utility.PermUtil


class EditAdsAct : AppCompatActivity(), FragmentCloseInterface {
    lateinit var binding: ActivityEditAdsBinding
    private val dialog = DialogSpinnerHelper()
    private var ispermisssionGranted = false
    lateinit var imageViewAdapter: ImageAdapter
    var chooseImageFrag: ImageListFragment? = null
    private val dbmanager = DbManager(null)
    var editimagepos = 0
    var launcherSeveralSelectImage:ActivityResultLauncher<Intent>? = null
    var launcherSingleSelectImage :ActivityResultLauncher<Intent>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditAdsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PermUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    ImagePiker.
                } else {
                    Toast.makeText(this, "Approve perm", Toast.LENGTH_LONG)
                }
                return
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    private fun init() {

        imageViewAdapter = ImageAdapter()
        binding.vpImages.adapter = imageViewAdapter
        launcherSeveralSelectImage = ImagePiker.getLauncherForSeveralImages(this)
        launcherSingleSelectImage = ImagePiker.getLauncherForSingleImage(this)
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
            Toast.makeText(this, "No country selected", Toast.LENGTH_LONG).show()
        }
    }
    fun onClickSelectCat(view:View){

        val listCity = resources.getStringArray(R.array.category).toMutableList() as ArrayList
        dialog.showSpinnerDialog(this, listCity, binding.selectCategory)

    }

    fun onClickGetImages(view: View) {
        if (imageViewAdapter.array.size == 0){

            ImagePiker.launcher(this,launcherSeveralSelectImage,3)

        } else {
            openChoosenImageFrag(null)
            chooseImageFrag?.updateAdapterFromEdit(imageViewAdapter.array)
        }
    }

    fun onClickPublish(view:View){


        dbmanager.publishOffer(filloffer())
        Log.d("asdvxzcvzxzv","send firebase")

    }
    fun filloffer():Offer{
        val offer:Offer
        binding.apply {
            offer = Offer(
                binding.editTitleOffer.text.toString(),
                binding.selectCountry.text.toString(),
                binding.selectCity.text.toString(),
                binding.phoneEditText.text.toString(),
                binding.adresseditText.text.toString(),
                binding.selectCategory.text.toString(),
                binding.priceeditrext.text.toString(),
                binding.editTextdiscription.text.toString(),
                dbmanager.db.push().key // генерируем уникальный ключ офера
            )
        }
        return offer
    }

    override fun onFragClose(list: ArrayList<Bitmap>) {
        binding.scrolview.visibility = View.VISIBLE
        imageViewAdapter.update(list)
        chooseImageFrag = null
    }

    fun openChoosenImageFrag(newlist:ArrayList<String>?){
        chooseImageFrag = ImageListFragment(this, newlist)
        binding.scrolview.visibility = View.GONE
        supportFragmentManager.beginTransaction()
            .replace(R.id.placeholder, chooseImageFrag!!)
            .commit()
    }
}