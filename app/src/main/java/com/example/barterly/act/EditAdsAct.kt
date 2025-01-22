 package com.example.barterly.act

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.PackageManagerCompat
import com.example.barterly.R
import com.example.barterly.databinding.ActivityEditAdsBinding
import com.example.barterly.dialoghelper.DialogConst
import com.example.barterly.dialoghelper.DialogHelper
import com.example.barterly.dialogs.DialogSpinnerHelper
import com.example.barterly.utils.CityHelper
import com.example.barterly.utils.ImagePiker
import com.fxn.pix.Pix
import com.fxn.utility.PermUtil


 class EditAdsAct : AppCompatActivity() {
     lateinit var binding: ActivityEditAdsBinding
    private val dialog = DialogSpinnerHelper()
    private var ispermisssionGranted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =ActivityEditAdsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

     override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
         super.onActivityResult(requestCode, resultCode, data)
         if (requestCode == RESULT_OK && requestCode == ImagePiker.REQuest_CODE_GET_IMAGES){
             val returnvalue:ArrayList<String>
             if (data != null) {
                 data.getStringArrayExtra(Pix.IMAGE_RESULTS)
             }
         }
     }

     @SuppressLint("MissingSuperCall")
     override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
         when(requestCode){
             PermUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS ->{
                 if (grantResults.size>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    ImagePiker.getImages(this)
                 }else{
                     Toast.makeText(this,"Approve perm",Toast.LENGTH_LONG)
                 }
                 return
             }
         }
     }


     private fun init(){
     }
     //OnClicks
     fun onClickSelectCountry(view:View){
         if (binding.selectCity.text != getString(R.string.select_city)){
             binding.selectCity.text = getString(R.string.select_city)
         }
         val listCountry = CityHelper.getAllCountries(this)
         dialog.showSpinnerDialog(this,listCountry,binding.selectCountry)

     }
     fun onClickSelectCity(view:View){
         var city =binding.selectCountry.text.toString()
         if (city != view.context.getString(R.string.select_country)) {
             val listCity = CityHelper.getAllCities(this, city)
             dialog.showSpinnerDialog(this, listCity,binding.selectCity)
         } else{
             Toast.makeText(this,"No countryselected",Toast.LENGTH_LONG).show()
         }
     }
     fun onClickGetImages(view:View){
         var per =0
         ImagePiker.getImages(this)
     }

}