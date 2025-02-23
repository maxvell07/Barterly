package com.example.barterly.adapters

import androidx.recyclerview.widget.DiffUtil
import com.example.barterly.model.OfferResult

class DiffUtilHelper(val oldlist: ArrayList<OfferResult>, val newlist: List<OfferResult>): DiffUtil.Callback() { // обновляем только изменения (для анимации в списке оферов при deleteoffer)
    override fun getOldListSize(): Int {
        return oldlist.size
    }

    override fun getNewListSize(): Int {
        return newlist.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldlist[oldItemPosition].key == newlist[newItemPosition].key
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldlist[oldItemPosition] == newlist[newItemPosition]
    }
}