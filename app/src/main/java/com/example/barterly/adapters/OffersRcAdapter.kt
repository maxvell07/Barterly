package com.example.barterly.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.barterly.data.Offer
import com.example.barterly.databinding.CardItemBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase

class OffersRcAdapter(val auth:FirebaseAuth):RecyclerView.Adapter<OffersRcAdapter.OfferViewHolder>() {
    val offerArray = ArrayList<Offer>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OfferViewHolder {
        val binding = CardItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return OfferViewHolder(binding,auth)
    }

    override fun getItemCount(): Int {
        return offerArray.size
    }

    override fun onBindViewHolder(holder: OfferViewHolder, position: Int) {
        holder.setData(offerArray[position])
    }

    fun updateAdapter(arr: List<Offer>){
        offerArray.clear()
        offerArray.addAll(arr)
        notifyDataSetChanged()

    }

    class OfferViewHolder(val binding: CardItemBinding,val auth:FirebaseAuth) :RecyclerView.ViewHolder(binding.root) {

        fun setData(offer:Offer){
            binding.apply {
                cardtitle.text = offer.title
                pricetitle.text = offer.price
                descriptiontitle.text = offer.description
                }
            showEditPanel(isOwner(offer))
        }

        private fun isOwner(offer:Offer):Boolean{
            return offer.uid == auth.uid
        }
        private fun showEditPanel(isOwner:Boolean){
            if (isOwner){
                binding.editOfferPanel.visibility=View.VISIBLE
            } else {
                binding.editOfferPanel.visibility=View.GONE
            }
        }
    }

}
