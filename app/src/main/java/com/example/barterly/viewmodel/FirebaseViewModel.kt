package com.example.barterly.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.barterly.model.DbManager
import com.example.barterly.model.Offer
import com.example.barterly.model.ReadDataCallback

class FirebaseViewModel : ViewModel() {

    private val dbManager = DbManager()
    val liveOffersData = MutableLiveData<ArrayList<Offer>>()

    fun loadoffers() {
        dbManager.getAllOffers(object : ReadDataCallback {
            override fun readData(list: ArrayList<Offer>) {
                liveOffersData.value = list
            }
        })
    }

    fun loadMyOffers() {
        dbManager.getMyOffers(object : ReadDataCallback {
            override fun readData(list: ArrayList<Offer>) {
                liveOffersData.value = list
            }
        })
    }
}