package com.example.barterly.database

import com.example.barterly.data.Offer

interface ReadDataCallback {

    fun readData(list:List<Offer>)
}