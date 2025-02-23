package com.example.barterly.model

import com.example.barterly.service.FileRepository
import com.example.barterly.service.RetrofitClient
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class DbManager {

    val db =  Firebase.database.getReference(MAIN_NOTE)
    val auth = Firebase.auth

    fun publishOffer(offer:Offer, finishLoadListener: finishLoadListener) {
       if (auth.uid !=null) {
           db.child(offer.key ?: "empty").child(auth.uid!!)
               .child(OFFER_NOTE)
               .setValue(offer).addOnCompleteListener{
                finishLoadListener.onFinish(true)
               }
       }
    }

    fun offerViewed(offer: Offer){
        var counter = offer.viewcounter.toInt() + 1

        if(auth.uid != null){
            db.child(offer.key ?: "empty")
                .child(INFO_NOTE)
                .setValue(InfoItem(
                    viewsCounter = counter.toString(),
                    emailsCounter = offer.emailcounter,
                    callsCounter = offer.callscounter
                ))
        }
    }

    fun onFavClick(offer: Offer,finishLoadListener: finishLoadListener){
        if (offer.isFav){
            removefromFavs(offer,finishLoadListener)
        }else{
            addtoFavs(offer,finishLoadListener)
        }
    }

    private fun addtoFavs(offer: Offer, listener: finishLoadListener) {
        offer.key?.let { key ->
            auth.uid?.let { uid ->
                db.child(key).child(FAVS_NOTE).child(uid).setValue(uid).addOnCompleteListener {
                    if (it.isSuccessful) listener.onFinish(true)
                }
            }
        }
    }
    private fun removefromFavs(offer: Offer,listener: finishLoadListener){
        offer.key?.let { // создаем узел favs и записываем в него uid пользователя
            offer.uid?.let { uid -> db.child(it).child(FAVS_NOTE).child(uid).removeValue().addOnCompleteListener{
                if (it.isSuccessful) listener.onFinish(true)
            } }
        }
    }

    fun getMyOffers( readCallback: ReadDataCallback?){
        val query = db.orderByChild(auth.uid + "/offer/uid").equalTo(auth.uid)// путь для фильтрации своих оферов
        readDataFromDb(query, readCallback)
    }

    fun getMyFavs( readCallback: ReadDataCallback?){
        val query = db.orderByChild( "/favs/${auth.uid}").equalTo(auth.uid)// путь для фильтрации своих оферов
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
                listener.onFinish(true)
            }
        }
    }

  private  fun readDataFromDb(query: Query, readCallback : ReadDataCallback?){ // слушатель изменения данных

        query.addListenerForSingleValueEvent(object :ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) { //snapshot все объявления по пути main
                val offerarray =  ArrayList<Offer>()

                for (item in snapshot.children){

                    var offer:Offer? = null

                    item.children.forEach {
                        if(offer == null) offer = it.child(OFFER_NOTE).getValue(Offer::class.java)
                    }
                    var infoItem = item.child(INFO_NOTE).getValue(InfoItem::class.java)
                    val favsCounter = item.child(FAVS_NOTE).childrenCount.toString()
                    val isFavs = auth.uid?.let { item.child(FAVS_NOTE).child(it).getValue(String::class.java) }
                    offer?.apply {
                        isFav = isFavs != null
                        viewcounter = infoItem?.viewsCounter ?: "0"
                        emailcounter = infoItem?.emailsCounter ?: "0"
                         callscounter= infoItem?.callsCounter ?: "0"
                        favCounter =favsCounter
                    }
                    if(offer != null) offerarray.add(offer!!)
                }
                readCallback?.readData(offerarray)
            }

            override fun onCancelled(error: DatabaseError) {}

        })
    }

    companion object {
        const val OFFER_NOTE = "offer"
        const val MAIN_NOTE = "main"
        const val INFO_NOTE = "info"
        const val FAVS_NOTE = "favs"
    }
}

interface ReadDataCallback {

    fun readData(list:ArrayList<Offer>)
}
interface finishLoadListener {
    fun onFinish(bol:Boolean)
}
