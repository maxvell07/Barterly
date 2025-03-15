package com.example.barterly.model

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
