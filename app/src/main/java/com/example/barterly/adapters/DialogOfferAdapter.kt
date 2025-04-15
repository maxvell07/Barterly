package com.example.barterly.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.barterly.R
import com.example.barterly.model.Offer
import com.squareup.picasso.Picasso

class DialogOfferAdapter(
    private val offers: List<Offer?>,
    private val onClick: (Offer) -> Unit
) : RecyclerView.Adapter<DialogOfferAdapter.DialogOfferViewHolder>() {

    inner class DialogOfferViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivImage: ImageView = view.findViewById(R.id.offerImage)
        val tvTitle: TextView = view.findViewById(R.id.offerTitle)
        val tvPrice: TextView = view.findViewById(R.id.offerPrice)

        fun bind(offer: Offer?) {
            tvTitle.text = offer?.title ?: "Без названия"
            tvPrice.text = offer?.price?.plus(" ₽") ?: "0 ₽"

            if (!offer?.img1.isNullOrEmpty()) {
                Picasso.get()
                    .load(offer.img1)
                    .placeholder(R.drawable.image)
                    .error(R.drawable.image)
                    .into(ivImage)
            } else {
                ivImage.setImageResource(R.drawable.image)
            }

            itemView.setOnClickListener { offer?.let { p1 -> onClick(p1) } }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DialogOfferViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_offer_for_trade, parent, false)
        return DialogOfferViewHolder(view)
    }

    override fun onBindViewHolder(holder: DialogOfferViewHolder, position: Int) {
        holder.bind(offers[position])
    }

    override fun getItemCount(): Int = offers.size
}
