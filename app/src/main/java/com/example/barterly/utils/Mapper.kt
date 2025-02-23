package com.example.barterly.utils

import android.graphics.Bitmap
import com.example.barterly.model.Offer
import com.example.barterly.model.OfferResult

object Mapper {
    fun mapOfferToOfferResult(offer: Offer, img1: Bitmap?, img2: Bitmap?, img3: Bitmap?): OfferResult {
        return OfferResult(
            title = offer.title,
            country = offer.country,
            city = offer.city,
            phone = offer.phone,
            adress = offer.adress,
            category = offer.category,
            price = offer.price,
            description = offer.description,
            key = offer.key,
            uid = offer.uid,
            isFav = offer.isFav,
            favCounter = offer.favCounter,
            viewcounter = offer.viewcounter,
            emailcounter = offer.emailcounter,
            callscounter = offer.callscounter,
            img1 = null,
            img2 = null,
            img3 = null
        )
    }
    fun mapOfferResultToOffer(offerResult: OfferResult): Offer {
        return Offer(
            title = offerResult.title,
            country = offerResult.country,
            city = offerResult.city,
            phone = offerResult.phone,
            adress = offerResult.adress,
            category = offerResult.category,
            price = offerResult.price,
            description = offerResult.description,
            key = offerResult.key,
            uid = offerResult.uid,
            isFav = offerResult.isFav,
            favCounter = offerResult.favCounter,
            viewcounter = offerResult.viewcounter,
            emailcounter = offerResult.emailcounter,
            callscounter = offerResult.callscounter
        )
    }

}