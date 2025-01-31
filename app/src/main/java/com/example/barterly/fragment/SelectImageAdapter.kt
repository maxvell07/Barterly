package com.example.barterly.fragment

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.barterly.R
import com.example.barterly.act.EditAdsAct
import com.example.barterly.utils.ImagePiker
import com.example.barterly.utils.ItemTouchMoveCallback

class SelectImageAdapter:RecyclerView.Adapter<SelectImageAdapter.ImageHolder>(),ItemTouchMoveCallback.ItemTouchChangeAdapter {

    var list = ArrayList<Bitmap>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.select_image_frag_item, parent, false)
        return ImageHolder(view, parent.context,this)
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

    class ImageHolder(itemView: View, val context:Context, val adapter:SelectImageAdapter):RecyclerView.ViewHolder(itemView){
        lateinit var tv:TextView
        lateinit var image:ImageView
        lateinit var editImageButton: ImageButton
        lateinit var deleteImageButton: ImageButton
        lateinit var loadbar: ProgressBar

        fun setData(item:Bitmap){
            editImageButton = itemView.findViewById(R.id.btEditImage)
            deleteImageButton = itemView.findViewById(R.id.btdeleteimage)
            tv = itemView.findViewById(R.id.textTitle)
            image = itemView.findViewById(R.id.imageContent)
            loadbar = itemView.findViewById(R.id.loadbarimage)

            editImageButton.setOnClickListener{
                ImagePiker.getImages(context as EditAdsAct,1,ImagePiker.REQUEST_CODE_GET_SINGLE_IMAGE)
                context.editimagepos =adapterPosition

            }
            deleteImageButton.setOnClickListener{
                adapter.list.removeAt(adapterPosition)
                adapter.notifyItemRemoved(adapterPosition)
                for(n in 0 until adapter.list.size){ //для обновления списка с анимацией
                    adapter.notifyItemChanged(n)
                }
            }


            tv.text = context.resources.getStringArray(R.array.title_array)[adapterPosition]
            image.setImageBitmap(item)
        }

    }

    fun updateAdapter(newlist : List<Bitmap>, needclearorno:Boolean){
        if (needclearorno) list.clear()
        list.addAll(newlist)
        notifyDataSetChanged()
    }



}