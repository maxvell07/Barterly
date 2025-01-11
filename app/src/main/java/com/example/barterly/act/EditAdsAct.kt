 package com.example.barterly.act

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.barterly.databinding.ActivityEditAdsBinding
import com.example.barterly.dialoghelper.DialogConst
import com.example.barterly.dialoghelper.DialogHelper
import com.example.barterly.dialogs.DialogSpinnerHelper
import com.example.barterly.utils.CityHelper

 class EditAdsAct : AppCompatActivity() {
    private lateinit var binding: ActivityEditAdsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =ActivityEditAdsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val listCountry = CityHelper.getAllCountries(this)
        val dialog = DialogSpinnerHelper()
        dialog.showSpinnerDialog(this,listCountry)
    }
}