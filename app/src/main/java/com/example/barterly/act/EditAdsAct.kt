 package com.example.barterly.act

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.barterly.R
import com.example.barterly.databinding.ActivityEditAdsBinding
import com.example.barterly.databinding.ActivityMainBinding

 class EditAdsAct : AppCompatActivity() {
    private lateinit var binding: ActivityEditAdsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =ActivityEditAdsBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}