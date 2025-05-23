package com.example.barterly.data.model

import android.graphics.Bitmap
import java.io.Serializable

data class Offer(
    val country: String? = null,
    val city: String? = null,
    val phone: String? = null,
    val adress: String? = null,
    val category: String? = null,
    val title: String? = null,
    val price: String? = null,
    val description: String? = null,
    var img1: String? = null,
    var img2: String? = null,
    var img3: String? = null,
    val key: String? = null,
    var favCounter: String = "0",
    val uid: String? = null,
    val time: String ="0",

    var isFav: Boolean = false,

    var viewcounter: String = "0",
    var emailcounter: String = "0",
    var callscounter: String = "0"

) : Serializable

fun Offer.matches(criteria: FiltersCriteries): Boolean {
    criteria.category?.let { if (this.category != it) return false }
    criteria.city    ?.let { if (this.city     != it) return false }
    criteria.country ?.let { if (this.country  != it) return false }
    criteria.priceFrom?.let { this.price?.toIntOrNull()?.let { price -> if (price < it) return false } }
    criteria.priceTo  ?.let { this.price?.toIntOrNull()?.let { price -> if (price > it) return false } }
    return true
}

