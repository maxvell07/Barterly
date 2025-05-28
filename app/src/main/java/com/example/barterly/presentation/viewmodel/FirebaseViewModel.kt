package com.example.barterly.presentation.viewmodel

import androidx.lifecycle.LiveData
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
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


enum class OfferListType {
    ALL, MY, FAV
}

class FirebaseViewModel(val filerepository: FileRepository) : ViewModel() {

    private val dbManager = DbManager()
    val liveOffersData = MutableLiveData<ArrayList<Offer>?>()
    val myOffersData = MutableLiveData<ArrayList<Offer>?>()
    val favOffersData = MutableLiveData<ArrayList<Offer>?>()
    val filterslivedata = MutableLiveData<FiltersCriteries>(FiltersCriteries())
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading


    val liveDataFilter = MutableLiveData<ArrayList<Offer>?>()

    // Текущий активный тип списка
    var currentType: OfferListType = OfferListType.ALL
        private set

    fun clearFilters() {
        applyFilters(FiltersCriteries())
    }



    fun applyFilters(criteria: FiltersCriteries) {
        filterslivedata.postValue(criteria)

        if (currentType != OfferListType.ALL) {
            val listToShow: List<Offer> = when (currentType) {
                OfferListType.MY -> myOffersData.value ?: emptyList()
                OfferListType.FAV -> favOffersData.value ?: emptyList()
                else -> emptyList()
            }
            liveDataFilter.postValue(ArrayList(listToShow)) // ← исправлено
            return
        }

        val offersToFilter = liveOffersData.value ?: return

        val filtered = offersToFilter
            .filter { it.matches(criteria) }
            .let { list ->
                if (criteria.sortByTime == true) list.sortedByDescending { it.time } else list
            }

        liveDataFilter.postValue(ArrayList(filtered)) // ← это уже было правильно
    }




    // Устанавливаем текущий тип списка и сразу фильтруем по текущим критериям
    fun setCurrentType(type: OfferListType) {
        currentType = type
        val criteria = filterslivedata.value ?: FiltersCriteries()
        applyFilters(criteria)
    }

    fun loadoffers() {
        _isLoading.postValue(true)
        DbManager().getAllOffers(object : ReadDataCallback {
            override fun readData(list: MutableList<Offer>) {
                viewModelScope.launch(Dispatchers.IO) {
                    val host = ServerConnectionConstants.URL
                    val updated = list.map {
                        it.copy(
                            img1 = host + it.img1,
                            img2 = host + it.img2,
                            img3 = host + it.img3
                        )
                    }.reversed()
                    liveOffersData.postValue(updated as ArrayList<Offer>?)
                    if (currentType == OfferListType.ALL) {
                        applyFilters(filterslivedata.value ?: FiltersCriteries())
                    }
                    _isLoading.postValue(false) // когда всё загружено
                }
            }
        })
    }


    fun loadMyFavs() {
        _isLoading.postValue(true)
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
                    favOffersData.postValue(updated as ArrayList<Offer>?)
                    if (currentType == OfferListType.FAV) {
                        applyFilters(filterslivedata.value ?: FiltersCriteries())
                    }
                    _isLoading.postValue(false)
                }
            }
        })
    }

    fun loadMyOffers() {
        _isLoading.postValue(true)
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
                    myOffersData.postValue(updated as ArrayList<Offer>?)
                    if (currentType == OfferListType.MY) {
                        applyFilters(filterslivedata.value ?: FiltersCriteries())
                    }
                    _isLoading.postValue(false)
                }
            }
        })
    }

    fun deleteoffer(offer: Offer) {
        val offer = offer // Конвертируем OfferResult в Offer
        dbManager.deleteoffer(offer, object : finishLoadListener {
            override fun onFinish(bol: Boolean) {
                if (bol) {
                    val updatedList = liveDataFilter.value
                    updatedList?.remove(offer) // Удаляем по OfferResult
                    liveDataFilter.postValue(updatedList)
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
                val updatedList = liveDataFilter.value
                val pos = updatedList?.indexOf(offer)

                if (pos != null && pos != -1) {
                    val favCounter =
                        if (offer.isFav) offer.favCounter.toInt() - 1 else offer.favCounter.toInt() + 1

                    updatedList[pos] = updatedList[pos].copy(
                        isFav = !offer.isFav,
                        favCounter = favCounter.toString()
                    )
                }

                liveDataFilter.postValue(updatedList)
            }
        })
    }
}