package com.example.barterly.adapters

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.barterly.R
import com.example.barterly.act.DescriptionAct
import com.example.barterly.act.EditAdsAct
import com.example.barterly.act.MainActivity
import com.example.barterly.model.Offer
import com.example.barterly.databinding.CardItemBinding
import com.example.barterly.model.OfferResult
import com.example.barterly.utils.Mapper.mapOfferResultToOffer

class OffersRcAdapter(val act: MainActivity) :
    RecyclerView.Adapter<OffersRcAdapter.OfferViewHolder>() {
    val offerArray = ArrayList<OfferResult>()


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

    fun updateAdapter(arr: List<OfferResult>) {
        val difresul = DiffUtil.calculateDiff(DiffUtilHelper(offerArray, arr))
        difresul.dispatchUpdatesTo(this)
        offerArray.clear()
        offerArray.addAll(arr)

    }

    class OfferViewHolder(val binding: CardItemBinding, val act: MainActivity) :
        RecyclerView.ViewHolder(binding.root) {

        fun setData(offer: OfferResult) = with(binding) {
            cardtitle.text = offer.title
            pricetitle.text = offer.price
            descriptiontitle.text = offer.description
            tvViewCounter.text = offer.viewcounter
            tvFav.text = offer.favCounter
            isFav(offer)
            showEditPanel(isOwner(offer))
            mainonClick(offer)

            offer.img1?.let {
                mainimage.setImageBitmap(it)  // Устанавливаем изображение в ImageView
                Log.d("image", it.toString())
            }
        }
        private fun mainonClick(offer: OfferResult) = with(binding){
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
        private fun isFav(offer:OfferResult){
            if (offer.isFav) {
                binding.ibFav.setImageResource(R.drawable.ic_favorite_pressed)
            } else {
                binding.ibFav.setImageResource(R.drawable.ic_favorite_normal)
            }
        }



        private fun onClickEdit(offer: OfferResult): View.OnClickListener {
            return View.OnClickListener {
                val i = Intent(act, EditAdsAct::class.java).apply {
                    putExtra(MainActivity.EDIT_STATE, true)
                    putExtra(MainActivity.OFFER_KEY, offer.key)
                }
                act.startActivity(i)

            }
        }

        private fun isOwner(offer: OfferResult): Boolean {
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

    fun onFavClick(offer: OfferResult)
    fun onOfferViewed(offer: OfferResult)

    fun ondeleteoffer(offer: OfferResult)

}
