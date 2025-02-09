package com.example.barterly.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.barterly.data.Offer
import com.example.barterly.databinding.CardItemBinding

class OffersRcAdapter:RecyclerView.Adapter<OffersRcAdapter.OfferViewHolder>() {
    val offerArray = ArrayList<Offer>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OfferViewHolder {
        val binding = CardItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return OfferViewHolder(binding)
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

    class OfferViewHolder(val binding: CardItemBinding) :RecyclerView.ViewHolder(binding.root) {

        fun setData(offer:Offer){
            binding.apply {
                cardtitle.text = offer.title
                pricetitle.text = offer.price
                descriptiontitle.text = offer.description
                }
        }

    }

}
