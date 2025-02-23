package com.example.barterly.model

import android.graphics.Bitmap
import java.io.Serializable

data class OfferResult(
    val title: String? = null,
    val country: String? = null,
    val city: String? = null,
    val phone: String? = null,
    val adress: String? = null,
    val category: String? = null,
    val price: String? = null,
    val description: String? = null,
    val key: String? = null,
    val uid: String? = null,
    var isFav: Boolean = false,
    var favCounter: String = "0",
    var img1: Bitmap? = null,
    var img2: Bitmap? = null,
    var img3: Bitmap? = null,

    var viewcounter: String = "0",
    var emailcounter: String = "0",
    var callscounter: String = "0"

) : Serializable