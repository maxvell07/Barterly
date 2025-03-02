package com.example.barterly.viewmodel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.barterly.model.DbManager
import com.example.barterly.model.Offer
import com.example.barterly.model.OfferResult
import com.example.barterly.model.ReadDataCallback
import com.example.barterly.model.finishLoadListener
import com.example.barterly.service.FileRepository
import com.example.barterly.utils.Mapper.mapOfferResultToOffer
import com.example.barterly.utils.Mapper.mapOfferToOfferResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FirebaseViewModel(val filerepository: FileRepository) : ViewModel() {

    private val dbManager = DbManager()
    val liveOffersData = MutableLiveData<ArrayList<OfferResult>>()

    //suspend
    fun loadoffers() {
        dbManager.getAllOffers(object : ReadDataCallback {
            override fun readData(list: MutableList<Offer>) {
                // Создаем новый список OfferResult с пустыми изображениями
                val mappedList =
                    list.map { offer -> mapOfferToOfferResult(offer, null, null, null) }
                viewModelScope.launch(Dispatchers.IO) {
                    val updatedList = mappedList.mapIndexed { index, offerResult ->
                        val img1 = getImage(offerResult.key.toString(), "img1.jpg")
                        val img2 = getImage(offerResult.key.toString(), "img2.jpg")
                        val img3 = getImage(offerResult.key.toString(), "img3.jpg")
                        offerResult.copy(img1 = img1, img2 = img2, img3 = img3)
                    }
                    // Обновляем LiveData
                    liveOffersData.postValue(ArrayList(updatedList))
                }
            }
        })
    }

    private fun fillViews(offer: OfferResult) {
        //observelive data
        //
        viewModelScope.launch(Dispatchers.IO) {
            val images = listOf(
                getImage(offer.key.toString(), "img1.jpg"),
                getImage(offer.key.toString(), "img2.jpg"),
                getImage(offer.key.toString(), "img3.jpg")
            )
        }
    }

    suspend fun getImage(userId: String, fileName: String): Bitmap? {
        return try {
            val response = filerepository.getFile(userId, fileName)
            if (response.isSuccessful) {
                response.body()?.let {
                    // Преобразуем байты в Bitmap
                    BitmapFactory.decodeStream(it.byteStream())
                }
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

    }

    // написать метод который вызывает loadoffers а после подгружпет картинки к оферам

    fun loadMyFavs() {
        dbManager.getMyFavs(object : ReadDataCallback {
            override fun readData(list: MutableList<Offer>) {
                // Мапим список Offer -> OfferResult (изображения пока null)
                val mappedList =
                    list.map { offer -> mapOfferToOfferResult(offer, null, null, null) }

                // Обновляем LiveData
                liveOffersData.postValue(ArrayList(mappedList))
            }
        })
    }

    fun loadMyOffers() {
        dbManager.getMyOffers(object : ReadDataCallback {
            override fun readData(list: MutableList<Offer>) {
                // Мапим список Offer -> OfferResult (изображения пока null)
                val mappedList =
                    list.map { offer -> mapOfferToOfferResult(offer, null, null, null) }

                // Обновляем LiveData
                liveOffersData.postValue(ArrayList(mappedList))
            }
        })
    }

    fun deleteoffer(offerResult: OfferResult) {
        val offer = mapOfferResultToOffer(offerResult) // Конвертируем OfferResult в Offer
        dbManager.deleteoffer(offer, object : finishLoadListener {
            override fun onFinish(bol: Boolean) {
                if (bol) {
                    val updatedList = liveOffersData.value
                    updatedList?.remove(offerResult) // Удаляем по OfferResult
                    liveOffersData.postValue(updatedList)
                }
            }
        })
    }


    fun offerViewed(offerResult: OfferResult) {
        val offer = mapOfferResultToOffer(offerResult) // Конвертируем OfferResult в Offer
        dbManager.offerViewed(offer)
    }

    fun onFavClick(offerResult: OfferResult) {
        val offer = mapOfferResultToOffer(offerResult) // Конвертируем OfferResult в Offer

        dbManager.onFavClick(offer, object : finishLoadListener {
            override fun onFinish(bol: Boolean) {
                val updatedList = liveOffersData.value
                val pos = updatedList?.indexOf(offerResult)

                if (pos != null && pos != -1) {
                    val favCounter =
                        if (offerResult.isFav) offerResult.favCounter.toInt() - 1 else offerResult.favCounter.toInt() + 1

                    updatedList[pos] = updatedList[pos].copy(
                        isFav = !offerResult.isFav,
                        favCounter = favCounter.toString()
                    )
                }

                liveOffersData.postValue(updatedList)
            }
        })
    }


}