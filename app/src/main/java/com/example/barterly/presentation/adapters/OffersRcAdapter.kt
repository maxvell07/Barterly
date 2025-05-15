package com.example.barterly.presentation.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.barterly.R
import com.example.barterly.presentation.view.act.DescriptionAct
import com.example.barterly.presentation.view.act.EditOfferAct
import com.example.barterly.presentation.view.act.MainActivity
import com.example.barterly.databinding.CardItemBinding
import com.example.barterly.data.model.Offer
import com.squareup.picasso.Picasso

class OffersRcAdapter(val act: MainActivity) :
    RecyclerView.Adapter<OffersRcAdapter.OfferViewHolder>() {
    val offerArray = ArrayList<Offer>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OfferViewHolder {
        val binding = CardItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OfferViewHolder(binding, act)
    }

    override fun getItemCount(): Int {
        return offerArray.size
    }

    override fun onBindViewHolder(holder: OfferViewHolder, position: Int) {
        holder.setData(offerArray[position])
    }

    fun updateAdapter(arr: List<Offer>) {
        val difresul = DiffUtil.calculateDiff(DiffUtilHelper(offerArray, arr))
        difresul.dispatchUpdatesTo(this)
        offerArray.clear()
        offerArray.addAll(arr)

    }

    override fun onViewRecycled(holder: OfferViewHolder) {
        super.onViewRecycled(holder)
        Picasso.get().cancelRequest(holder.binding.mainimage)
    }

    class OfferViewHolder(val binding: CardItemBinding, val act: MainActivity) :
        RecyclerView.ViewHolder(binding.root) {

        fun setData(offer: Offer) = with(binding) {
            cardtitle.text = offer.title
            pricetitle.text = offer.price
            descriptiontitle.text = offer.description
            tvViewCounter.text = offer.viewcounter
            tvFav.text = offer.favCounter
            isFav(offer)
            showEditPanel(isOwner(offer))
            mainonClick(offer)

            offer.img1?.let { imageUrl ->
                Picasso.get()
                    .load(imageUrl)
                    .tag(this)
                    .placeholder(R.drawable.image) // Заглушка во время загрузки
                    .error(R.drawable.image) // Если ошибка загрузки
                    .into(mainimage) // Загружаем изображение в ImageView
            }
        }

        private fun mainonClick(offer: Offer) = with(binding){
            ibEditOffer.setOnClickListener(onClickEdit(offer))
            ibDeleteOffer.setOnClickListener {
                act.ondeleteoffer(offer)
            }
            itemView.setOnClickListener {
                val i = Intent(binding.root.context,DescriptionAct::class.java)
                i.putExtra(MainActivity.OFFER_KEY,offer.key.toString())
                act.onOfferViewed(offer)
                binding.root.context.startActivity(i)
                Toast.makeText(act, "click", Toast.LENGTH_SHORT).show()
            }

            ibFav.setOnClickListener {
                if (act.myAuth.currentUser?.isAnonymous == true) {
                    Toast.makeText(act, "Зарегистрируйтесь", Toast.LENGTH_SHORT).show()
                } else {
                    act.onFavClick(offer)
                }
            }

        }
        private fun isFav(offer:Offer){
            if (offer.isFav) {
                binding.ibFav.setImageResource(R.drawable.ic_favorite_pressed)
            } else {
                binding.ibFav.setImageResource(R.drawable.ic_favorite_normal)
            }
        }



        private fun onClickEdit(offer: Offer): View.OnClickListener {
            return View.OnClickListener {
                val i = Intent(act, EditOfferAct::class.java).apply {
                    putExtra(MainActivity.EDIT_STATE, true)
                    putExtra(MainActivity.OFFER_KEY, offer.key)
                }
                act.startActivity(i)

            }
        }

        private fun isOwner(offer: Offer): Boolean {
            return offer.uid == act.myAuth.uid
        }

        private fun showEditPanel(isOwner: Boolean) {
            if (isOwner) {
                binding.editOfferPanel.visibility = View.VISIBLE
            } else {
                binding.editOfferPanel.visibility = View.GONE
            }
        }
    }
}

interface offerlistener {

    fun onFavClick(offer: Offer)
    fun onOfferViewed(offer: Offer)

    fun ondeleteoffer(offer: Offer)

}
