package com.example.barterly.fragment

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.barterly.R

class SelectImageAdapter:RecyclerView.Adapter<SelectImageAdapter.ImageHolder>() {

    var list = ArrayList<SelectImageItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.select_image_frag_item, parent, false)
        return ImageHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ImageHolder, position: Int) {
        holder.setData(list[position])
    }

    class ImageHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        lateinit var tv:TextView
        lateinit var image:ImageView
        fun setData(item:SelectImageItem){
            tv = itemView.findViewById(R.id.textTitle)
            image = itemView.findViewById(R.id.imageContent)
            tv.text = item.title
            image.setImageURI(Uri.parse(item.image))

        }

    }

    fun updateAdapter(newlist : List<SelectImageItem>){
        list.clear()
        list.addAll(newlist)
        notifyDataSetChanged()
    }
}