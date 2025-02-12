package com.example.barterly.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.barterly.model.DbManager
import com.example.barterly.model.Offer
import com.example.barterly.model.ReadDataCallback
import com.example.barterly.model.finishLoadListener

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
        dbManager.getMyOffers( object : ReadDataCallback {
            override fun readData(list: ArrayList<Offer>) {
                liveOffersData.value = list
            }
        })
    }
    fun deleteoffer(offer: Offer){
        dbManager.deleteoffer(offer,object :finishLoadListener{
            override fun onFinish() {
                val updatedlist = liveOffersData.value
                updatedlist?.remove(offer)
                liveOffersData.postValue(updatedlist)
            }

        })
    }
}