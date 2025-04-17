package com.example.barterly.util.dialogs

import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.barterly.R

class RecyclerDialogSpinner(var tvSelection:TextView, var dialog: AlertDialog) : RecyclerView.Adapter<RecyclerDialogSpinner.SpViewHolder>() {

    private val mainlist = ArrayList<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.sp_list_item, parent, false)
        return SpViewHolder(view, tvSelection,dialog)
    }

    override fun getItemCount(): Int {
        return mainlist.size
    }

    override fun onBindViewHolder(holder: SpViewHolder, position: Int) {
        holder.setData(mainlist[position])
    }


    class SpViewHolder(itemView: View, var tvSelection: TextView, var dialog: AlertDialog) : RecyclerView.ViewHolder(itemView),
        OnClickListener {

        private var sctext = ""
        fun setData(text: String) {
            val tvSpitem = itemView.findViewById<TextView>(R.id.tvSpitem)
            tvSpitem.text = text
            sctext = text
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            tvSelection.text = sctext
            dialog.dismiss()
        }
    }

    fun updateAdapter(list: ArrayList<String>) {
        mainlist.clear()
        mainlist.addAll(list)
        notifyDataSetChanged()//сообщаем адаптеру что данные изменились
    }
}