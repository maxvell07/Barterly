package com.example.Barterly.myapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
//import com.example.barterly.OnCardClickListener
import com.example.barterly.R

class CardAdapter(
    private val items: List<CardItem>,
//    private val listener: OnCardClickListener
) : RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    class CardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageView)
        val textView: TextView = view.findViewById(R.id.textView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_card, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val item = items[position]
        holder.imageView.setImageResource(item.imageResId)
        holder.textView.text = item.title

        holder.itemView.setOnClickListener {
//            listener.onCardClick(position) // Передаём позицию
        }
    }

    override fun getItemCount() = items.size
}

data class CardItem(val imageResId: Int, val title: String)
