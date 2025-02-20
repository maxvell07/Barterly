package com.example.barterly.model

import java.io.Serializable

data class Offer(
    val title :String? = null,
    val country:String? = null,
    val city:String? = null,
    val phone:String? = null,
    val adress:String? = null,
    val category:String? = null,
    val price:String? = null,
    val description:String? = null,
    val mainImage:String? = null,
    val key:String? = null,
    val uid:String? = null,
    var isFav:Boolean = false,
    var favCounter: String = "0",

    var viewcounter:String = "0",
    var emailcounter:String = "0",
    var callscounter:String = "0"

): Serializable
