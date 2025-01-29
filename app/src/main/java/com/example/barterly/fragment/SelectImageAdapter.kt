package com.example.barterly.fragment

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.barterly.R
import com.example.barterly.utils.ItemTouchMoveCallback

class SelectImageAdapter:RecyclerView.Adapter<SelectImageAdapter.ImageHolder>(),ItemTouchMoveCallback.ItemTouchChangeAdapter {

    var list = ArrayList<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.select_image_frag_item, parent, false)
        return ImageHolder(view, parent.context)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onMove(startPos: Int, targetPos: Int) {
        val target = list[targetPos]
        list[targetPos] = list[startPos]
        list[startPos] = target

        notifyItemMoved(startPos,targetPos)

    }

    override fun onClear() {
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ImageHolder, position: Int) {
        holder.setData(list[position])
    }

    class ImageHolder(itemView: View, var context:Context):RecyclerView.ViewHolder(itemView){
        lateinit var tv:TextView
        lateinit var image:ImageView

        fun setData(item:String){
            tv = itemView.findViewById(R.id.textTitle)
            image = itemView.findViewById(R.id.imageContent)
            tv.text = context.resources.getStringArray(R.array.title_array)[adapterPosition]
            image.setImageURI(Uri.parse(item))

        }

    }

    fun updateAdapter(newlist : List<String>, needclearorno:Boolean){
        if (needclearorno) list.clear()
        list.addAll(newlist)
        notifyDataSetChanged()
    }


}