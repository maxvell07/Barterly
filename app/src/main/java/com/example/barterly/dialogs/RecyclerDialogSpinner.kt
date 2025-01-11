package com.example.barterly.dialogs

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.barterly.R

class RecyclerDialogSpinner : RecyclerView.Adapter<SpViewHolder>() {

    private val mainlist = ArrayList<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.sp_list_item, parent, false)
        return SpViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mainlist.size
    }

    override fun onBindViewHolder(holder: SpViewHolder, position: Int) {
        holder.setData(mainlist[position])
    }

    fun updateAdapter(list: ArrayList<String>) {
        mainlist.clear()
        mainlist.addAll(list)
        notifyDataSetChanged()//сообщаем адаптеру что данные изменились
    }

}

class SpViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


    fun setData(text: String) {
        val tvSpitem = itemView.findViewById<TextView>(R.id.tvSpitem)
        tvSpitem.text = text
    }
}
