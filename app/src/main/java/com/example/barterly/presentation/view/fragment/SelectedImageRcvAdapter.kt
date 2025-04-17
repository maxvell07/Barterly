package com.example.barterly.presentation.view.fragment

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.barterly.R
import com.example.barterly.presentation.view.act.EditOfferAct
import com.example.barterly.databinding.SelectImageFragItemBinding
import com.example.barterly.util.AdapterDeleteCallback
import com.example.barterly.util.ImageManager
import com.example.barterly.util.ImagePiker
import com.example.barterly.util.ItemTouchMoveCallback

class SelectedImageRcvAdapter(val adapterDeleteCallback: AdapterDeleteCallback) :RecyclerView.Adapter<SelectedImageRcvAdapter.ImageHolder>(),ItemTouchMoveCallback.ItemTouchChangeAdapter {

    var list = ArrayList<Bitmap>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder {
        val viewBinding = SelectImageFragItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ImageHolder(viewBinding, parent.context,this)
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

    class ImageHolder(val viewBinding: SelectImageFragItemBinding, val context:Context, val adapter:SelectedImageRcvAdapter):RecyclerView.ViewHolder(viewBinding.root){

        fun setData(item:Bitmap){

            viewBinding.btEditImage.setOnClickListener{
                ImagePiker.pickSingleImages(context as EditOfferAct)
                context.editimagepos =adapterPosition

            }
            viewBinding.btdeleteimage.setOnClickListener{
                adapter.list.removeAt(adapterPosition)

                adapter.notifyItemRemoved(adapterPosition)
                for(n in 0 until adapter.list.size){ //для обновления списка с анимацией
                    adapter.notifyItemChanged(n)
                }
                adapter.adapterDeleteCallback.onClickDel()
            }


            viewBinding.textTitle.text = context.resources.getStringArray(R.array.title_array)[adapterPosition]
            ImageManager.chooseScaleType(viewBinding.imageContent,item)
            viewBinding.imageContent.setImageBitmap(item)
        }

    }

    fun updateAdapter(newlist : List<Bitmap>, needclearorno:Boolean){
        if (needclearorno) list.clear()
        list.addAll(newlist)
        notifyDataSetChanged()
    }



}