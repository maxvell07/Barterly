package com.example.barterly.presentation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.barterly.R
import com.example.barterly.databinding.CardItemBinding
import com.example.barterly.data.model.Offer
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
class OffersRcAdapter(
    private val offerListener: OfferListener
) : RecyclerView.Adapter<OffersRcAdapter.OfferViewHolder>() {

    private val offerArray = ArrayList<Offer>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OfferViewHolder {
        val binding = CardItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OfferViewHolder(binding, offerListener)
    }

    override fun getItemCount(): Int = offerArray.size

    override fun onBindViewHolder(holder: OfferViewHolder, position: Int) {
        holder.bind(offerArray[position])
    }

    fun updateAdapter(arr: List<Offer>) {
        val diffResult = DiffUtil.calculateDiff(DiffUtilHelper(offerArray, arr))
        diffResult.dispatchUpdatesTo(this)
        offerArray.clear()
        offerArray.addAll(arr)
    }

    override fun onViewRecycled(holder: OfferViewHolder) {
        super.onViewRecycled(holder)
        Picasso.get().cancelRequest(holder.binding.mainimage)
    }

    class OfferViewHolder(
        val binding: CardItemBinding,
        private val listener: OfferListener
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(offer: Offer) = with(binding) {
            cardtitle.text = offer.title
            pricetitle.text = offer.price
            descriptiontitle.text = offer.description
            tvViewCounter.text = offer.viewcounter
            tvFav.text = offer.favCounter
            updateFavIcon(offer)
            showEditPanel(offer.uid == FirebaseAuth.getInstance().uid)

            ibEditOffer.setOnClickListener { listener.onEditOffer(offer) }
            ibDeleteOffer.setOnClickListener { listener.onDeleteOffer(offer) }
            ibFav.setOnClickListener {
                if (FirebaseAuth.getInstance().currentUser?.isAnonymous == true) {
                    Toast.makeText(root.context, "Зарегистрируйтесь", Toast.LENGTH_SHORT).show()
                } else {
                    listener.onFavClick(offer)
                }
            }
            itemView.setOnClickListener {
                listener.onOfferViewed(offer)
            }

            offer.img1?.let { imageUrl ->
                Picasso.get()
                    .load(imageUrl)
                    .placeholder(R.drawable.image)
                    .error(R.drawable.image)
                    .into(mainimage)
            }
        }

        private fun updateFavIcon(offer: Offer) {
            val iconRes = if (offer.isFav) R.drawable.ic_favorite_pressed
            else R.drawable.ic_favorite_normal
            binding.ibFav.setImageResource(iconRes)
        }

        private fun showEditPanel(isOwner: Boolean) {
            binding.editOfferPanel.visibility = if (isOwner) View.VISIBLE else View.GONE
        }
    }
}
interface OfferListener {
    fun onFavClick(offer: Offer)
    fun onOfferViewed(offer: Offer)
    fun onDeleteOffer(offer: Offer)
    fun onEditOffer(offer: Offer)
}
