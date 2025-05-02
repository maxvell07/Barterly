package com.example.barterly.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.barterly.constants.ServerConnectionConstants
import com.example.barterly.data.model.DbManager
import com.example.barterly.data.model.FiltersCriteries
import com.example.barterly.data.model.Offer
import com.example.barterly.data.model.ReadDataCallback
import com.example.barterly.data.model.finishLoadListener
import com.example.barterly.data.model.matches
import com.example.barterly.data.source.service.FileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FirebaseViewModel(val filerepository: FileRepository) : ViewModel() {

    private val dbManager = DbManager()
    val liveOffersData = MutableLiveData<ArrayList<Offer>?>()

    val filterslivedata = MutableLiveData<FiltersCriteries>(FiltersCriteries())


    fun applyFilters(criteria: FiltersCriteries) {
        filterslivedata.value = criteria
    }
    val filteredOffers: LiveData<List<Offer>> = MediatorLiveData<List<Offer>>().apply {
        fun recalc() {
            val offers   = liveOffersData.value.orEmpty()
            val criteria = filterslivedata.value ?: FiltersCriteries()
            // фильтрация
            var result = offers.filter { it.matches(criteria) }
            // сортировка по времени, если нужно
            if (criteria.sortByTime) {
                result = result.sortedByDescending { it.time /* или своё поле времени */ }
            }
            value = result
        }
        addSource(liveOffersData) { recalc() }
        addSource(filterslivedata) { recalc() }
    }

    fun loadoffers() {
        DbManager().getAllOffers(object : ReadDataCallback {
            override fun readData(list: MutableList<Offer>) {
                viewModelScope.launch(Dispatchers.IO) {
                    val host = ServerConnectionConstants.URL
                    val updated = list
                        .map {
                            it.copy(
                                img1 = host + it.img1,
                                img2 = host + it.img2,
                                img3 = host + it.img3
                            )
                        }
                        .reversed()
                    liveOffersData.postValue(updated as ArrayList<Offer>?)
                }
            }
        })
    }

    // написать метод который вызывает loadoffers а после подгружпет картинки к оферам

    fun loadMyFavs() {
        DbManager().getMyFavs(object : ReadDataCallback {
            override fun readData(list: MutableList<Offer>) {
                viewModelScope.launch(Dispatchers.IO) {
                    val host = ServerConnectionConstants.URL
                    val updated = list.map {
                        it.copy(
                            img1 = host + it.img1,
                            img2 = host + it.img2,
                            img3 = host + it.img3
                        )
                    }
                    liveOffersData.postValue(updated as ArrayList<Offer>?)
                }
            }
        })
    }

    fun loadMyOffers() {
        DbManager().getMyOffers(object : ReadDataCallback {
            override fun readData(list: MutableList<Offer>) {
                viewModelScope.launch(Dispatchers.IO) {
                    val host = ServerConnectionConstants.URL
                    val updated = list.map {
                        it.copy(
                            img1 = host + it.img1,
                            img2 = host + it.img2,
                            img3 = host + it.img3
                        )
                    }
                    liveOffersData.postValue(updated as ArrayList<Offer>?)
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