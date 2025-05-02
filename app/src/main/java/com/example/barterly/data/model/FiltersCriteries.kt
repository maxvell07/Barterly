package com.example.barterly.data.model

data class FiltersCriteries(
    val category: String? = null,
    val city:     String? = null,
    val country:  String? = null,
    val priceFrom: Int?     = null,
    val priceTo:   Int?     = null,
    val sortByTime: Boolean = false
)

