 package com.example.barterly.act

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.barterly.R
import com.example.barterly.databinding.ActivityEditAdsBinding
import com.example.barterly.dialoghelper.DialogConst
import com.example.barterly.dialoghelper.DialogHelper
import com.example.barterly.dialogs.DialogSpinnerHelper
import com.example.barterly.utils.CityHelper

 class EditAdsAct : AppCompatActivity() {
     lateinit var binding: ActivityEditAdsBinding
    private val dialog = DialogSpinnerHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =ActivityEditAdsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }
     private fun init(){
     }
     //OnClicks
     fun onClickSelectCountry(view:View){
         val listCountry = CityHelper.getAllCountries(this)
         dialog.showSpinnerDialog(this,listCountry)
     }
}