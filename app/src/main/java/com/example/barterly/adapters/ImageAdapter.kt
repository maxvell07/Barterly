package com.example.barterly.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.barterly.R
import com.example.barterly.fragment.SelectImageItem

class ImageAdapter:RecyclerView.Adapter<ImageAdapter.ImageHolder>() {

    val array = ArrayList<SelectImageItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.image_adapter_item,parent,false)
        return ImageHolder(view)
    }

    override fun getItemCount(): Int {
        return array.size
    }

    override fun onBindViewHolder(holder: ImageHolder, position: Int) {
        holder.setData(array[position].image)
    }

    class ImageHolder(itemView: View):RecyclerView.ViewHolder(itemView){

        lateinit var imitem:ImageView

        fun setData(link:String){
            imitem = itemView.findViewById(R.id.ViewPagerItem)
            imitem.setImageURI(Uri.parse(link))
        }
    }
    fun update(newlist:ArrayList<SelectImageItem>){
        array.clear()
        array.addAll(newlist)
        notifyDataSetChanged()
    }
}