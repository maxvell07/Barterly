package com.example.barterly.model

import android.content.Context
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class DbManager {

    val db =  Firebase.database.getReference("main")
    val auth = Firebase.auth

    fun publishOffer(offer:Offer, finishLoadListener: finishLoadListener) {

       if (auth.uid !=null) {
           db.child(offer.key ?: "empty").child(auth.uid!!)
               .child("offer")
               .setValue(offer).addOnCompleteListener{
                if (it.isSuccessful)
                finishLoadListener.onFinish()
               }

       }

    }

    fun getMyOffers( readCallback: ReadDataCallback?){
        val query = db.orderByChild(auth.uid + "/offer/uid").equalTo(auth.uid)// путь для фильтрации своих оферов
        readDataFromDb(query, readCallback)
    }

    fun getAllOffers( readCallback: ReadDataCallback?){
        val query = db.orderByChild(auth.uid + "/offer/price")
        readDataFromDb(query, readCallback)
    }

    fun deleteoffer(offer: Offer,listener: finishLoadListener){
        if (offer.key == null || offer.uid == null) return
        db.child(offer.key).child(offer.uid).removeValue().addOnCompleteListener{
            if (it.isSuccessful){
                listener.onFinish()
            }
        }
    }

  private  fun readDataFromDb(query: Query, readCallback : ReadDataCallback?){ // слушатель изменения данных

        query.addListenerForSingleValueEvent(object :ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {
                val offerarray =  ArrayList<Offer>()

                for (item in snapshot.children){
                    val offer = item.children.iterator().next().child("offer").getValue(Offer::class.java)//получаем данные в виде объекта
                    if (offer!=null) offerarray.add(offer)
                }
                readCallback?.readData(offerarray)
            }

            override fun onCancelled(error: DatabaseError) {}

        })
    }

}

interface ReadDataCallback {

    fun readData(list:ArrayList<Offer>)
}
interface finishLoadListener {
    fun onFinish()
}
