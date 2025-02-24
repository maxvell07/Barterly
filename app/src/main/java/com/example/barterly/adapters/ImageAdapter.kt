package com.example.barterly.adapters

import android.graphics.Bitmap
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.barterly.R

class ImageAdapter:RecyclerView.Adapter<ImageAdapter.ImageHolder>() {

    val array = ArrayList<Bitmap>(3)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.image_adapter_item,parent,false)
        return ImageHolder(view)
    }

    override fun getItemCount(): Int {
        return array.size
    }

    override fun onBindViewHolder(holder: ImageHolder, position: Int) {
        holder.setData(array[position])
    }

    class ImageHolder(itemView: View):RecyclerView.ViewHolder(itemView){

        lateinit var imitem:ImageView

        fun setData(bitmap:Bitmap){
            imitem = itemView.findViewById(R.id.ViewPagerItem)
            imitem.setImageBitmap(bitmap)
        }
    }
    fun update(newlist:ArrayList<Bitmap>){
        array.clear()
        array.addAll(newlist)
        notifyDataSetChanged()
    }
}