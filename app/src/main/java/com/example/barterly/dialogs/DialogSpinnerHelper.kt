package com.example.barterly.dialogs

import android.content.Context
import android.view.LayoutInflater
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.barterly.R
import com.example.barterly.utils.CityHelper

class DialogSpinnerHelper {
    fun showSpinnerDialog(context: Context, list: ArrayList<String>,tvselection:TextView) {
        val builder = AlertDialog.Builder(context)
        val dialog = builder.create()
        val rootView = LayoutInflater.from(context).inflate(R.layout.spinner_layout, null)
        val adapter = RecyclerDialogSpinner(tvselection,dialog)
        val rcview = rootView.findViewById<RecyclerView>(R.id.rcSpView)
        val srView = rootView.findViewById<SearchView>(R.id.svSpinner)
        rcview.layoutManager = LinearLayoutManager(context)
        rcview.adapter = adapter
        dialog.setView(rootView)
        adapter.updateAdapter(list)
        setSearchView(adapter, list, srView)

        dialog.show()
    }

    private fun setSearchView(
        adapter: RecyclerDialogSpinner,
        list: ArrayList<String>,
        srView: SearchView
    ) {
        srView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val newlist = CityHelper.filterListData(list, newText)
                adapter.updateAdapter(newlist)
                return true
            }

        })
    }

}