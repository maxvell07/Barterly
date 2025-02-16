package com.example.barterly.fragment

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.barterly.R
import com.example.barterly.act.EditAdsAct
import com.example.barterly.databinding.SelectImageFragItemBinding
import com.example.barterly.utils.AdapterDeleteCallback
import com.example.barterly.utils.ImageManager
import com.example.barterly.utils.ImagePiker
import com.example.barterly.utils.ItemTouchMoveCallback

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
                ImagePiker.pickSingleImages(context as EditAdsAct)
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