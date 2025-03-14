package com.example.barterly.viewmodel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.barterly.constants.ServerConnectionConstants
import com.example.barterly.model.DbManager
import com.example.barterly.model.Offer
import com.example.barterly.model.ReadDataCallback
import com.example.barterly.model.finishLoadListener
import com.example.barterly.service.FileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FirebaseViewModel(val filerepository: FileRepository) : ViewModel() {

    private val dbManager = DbManager()
    val liveOffersData = MutableLiveData<ArrayList<Offer>>()

    //suspend
    fun loadoffers() {
        dbManager.getAllOffers(object : ReadDataCallback {
            override fun readData(list: MutableList<Offer>) {
                // Создаем новый список OfferResult с пустыми изображениями
                viewModelScope.launch(Dispatchers.IO) {
                    val updatedList = list.mapIndexed { index, offer ->
                        val host  = ServerConnectionConstants.URL
                        val img1 = host+"/images/"+offer.key.toString()+"/img1.jpg"
                        val img2 = host+"/images/"+offer.key.toString()+"/img2.jpg"
                        val img3 = host+"/images/"+offer.key.toString()+"/img3.jpg"
                        offer.copy(img1 = img1, img2 = img2, img3 = img3)
                    }
                    // Обновляем LiveData
                    liveOffersData.postValue(ArrayList(updatedList))
                }
            }
        })
    }

    // написать метод который вызывает loadoffers а после подгружпет картинки к оферам

    fun loadMyFavs() {
        dbManager.getMyFavs(object : ReadDataCallback {
            override fun readData(list: MutableList<Offer>) {
                viewModelScope.launch(Dispatchers.IO) {
                    val updatedList = list.mapIndexed { index, offer ->
                        val host  = ServerConnectionConstants.URL
                        val img1 = host+"/images/"+offer.key.toString()+"/img1.jpg"
                        val img2 = host+"/images/"+offer.key.toString()+"/img2.jpg"
                        val img3 = host+"/images/"+offer.key.toString()+"/img3.jpg"
                        offer.copy(img1 = img1, img2 = img2, img3 = img3)
                    }
                    // Обновляем LiveData
                    liveOffersData.postValue(ArrayList(updatedList))
                }
            }
        })
    }

    fun loadMyOffers() {
        dbManager.getMyOffers(object : ReadDataCallback {
            override fun readData(list: MutableList<Offer>) {
                viewModelScope.launch(Dispatchers.IO) {
                    val updatedList = list.mapIndexed { index, offer ->
                        val host  = ServerConnectionConstants.URL
                        val img1 = host+"/images/"+offer.key.toString()+"/img1.jpg"
                        val img2 = host+"/images/"+offer.key.toString()+"/img2.jpg"
                        val img3 = host+"/images/"+offer.key.toString()+"/img3.jpg"
                        offer.copy(img1 = img1, img2 = img2, img3 = img3)
                    }
                    // Обновляем LiveData
                    liveOffersData.postValue(ArrayList(updatedList))
                }
            }
        })
    }

    fun deleteoffer(offer: Offer) {
        val offer = offer // Конвертируем OfferResult в Offer
        dbManager.deleteoffer(offer, object : finishLoadListener {
            override fun onFinish(bol: Boolean) {
                if (bol) {
                    val updatedList = liveOffersData.value
                    updatedList?.remove(offer) // Удаляем по OfferResult
                    liveOffersData.postValue(updatedList)
                }
            }
        })
    }


    fun offerViewed(offer: Offer) {
        val offer = offer // Конвертируем OfferResult в Offer
        dbManager.offerViewed(offer)
    }

    fun onFavClick(offer: Offer) {
        val offer = offer // Конвертируем OfferResult в Offer

        dbManager.onFavClick(offer, object : finishLoadListener {
            override fun onFinish(bol: Boolean) {
                val updatedList = liveOffersData.value
                val pos = updatedList?.indexOf(offer)

                if (pos != null && pos != -1) {
                    val favCounter =
                        if (offer.isFav) offer.favCounter.toInt() - 1 else offer.favCounter.toInt() + 1

                    updatedList[pos] = updatedList[pos].copy(
                        isFav = !offer.isFav,
                        favCounter = favCounter.toString()
                    )
                }

                liveOffersData.postValue(updatedList)
            }
        })
    }


}