package com.example.barterly.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.barterly.constants.ServerConnectionConstants
import com.example.barterly.data.model.DbManager
import com.example.barterly.data.model.Offer
import com.example.barterly.data.model.ReadDataCallback
import com.example.barterly.data.model.finishLoadListener
import com.example.barterly.data.source.service.FileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FirebaseViewModel(val filerepository: FileRepository) : ViewModel() {

    private val dbManager = DbManager()
    val liveOffersData = MutableLiveData<ArrayList<Offer>>()

//    fun clear() {
//        liveOffersData.value =
//    }
    //suspend
    fun loadoffers() {
        dbManager.getAllOffers(object : ReadDataCallback {
            override fun readData(list: MutableList<Offer>) {
                // Создаем новый список OfferResult с пустыми изображениями
                viewModelScope.launch(Dispatchers.IO) {
                    var updatedList = list.mapIndexed { index, offer ->
                        val host = ServerConnectionConstants.URL
                        val img1: String
                        val img2: String
                        val img3: String
                        img1 = host +offer.img1
                        img2 = host +offer.img2
                        img3 = host +offer.img3
                        offer.copy(img1 = img1, img2 = img2, img3 = img3)
                    }
                    updatedList = updatedList.reversed()
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
                        val host = ServerConnectionConstants.URL
                        val img1 = host + offer.img1
                        val img2 = host + offer.img2
                        val img3 = host + offer.img3
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
                        val host = ServerConnectionConstants.URL
                        val img1 = host + offer.img1
                        val img2 = host + offer.img2
                        val img3 = host + offer.img3
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
        deleteAllImages(offer.key.toString())
    }

    fun deleteAllImages(key: String) {
        viewModelScope.launch {
            filerepository.deleteAllFiles(key.toString())
        }
    }

    fun offerViewed(offer: Offer) {
        val offer = offer // Конвертируем OfferResult в Offer
        dbManager.offerViewed(offer)
    }

    fun onFavClick(offer: Offer) {

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