package com.example.barterly.database

import com.example.barterly.data.Offer
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class DbManager (val readCallback : ReadDataCallback?){

    val db =  Firebase.database.getReference("main")
    val auth = Firebase.auth

    fun publishOffer(offer:Offer) {

       if (auth.uid !=null) {
           db.child(offer.key ?: "empty").child(auth.uid!!).child("offer").setValue(offer)
       }

    }
    fun readDataFromDb(){ // слушатель изменения данных

        db.addListenerForSingleValueEvent(object :ValueEventListener{

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
