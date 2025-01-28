package com.example.barterly.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.barterly.R
import com.example.barterly.utils.ItemTouchMoveCallback

class ImageListFragment(val fragClose:FragmentCloseInterface, private  val newlist: ArrayList<String>) : Fragment() {

    val adapter = SelectImageAdapter()
    val dragcallback = ItemTouchMoveCallback(adapter)
    val touchHelper = ItemTouchHelper(dragcallback)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.list_image_frag, container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var buttback = view.findViewById<Button>(R.id.button_frag)
        var rcview = view.findViewById<RecyclerView>(R.id.rcvSelectImage)
        touchHelper.attachToRecyclerView(rcview)
        rcview.layoutManager = LinearLayoutManager(activity)
        rcview.adapter =adapter
        val updatelist = ArrayList<SelectImageItem>()
        for (n in 0 until newlist.size ){
            val selectImageItem = SelectImageItem("0","0")
            updatelist.add(SelectImageItem(n.toString(),newlist[n]))
        }
        adapter.updateAdapter(updatelist)
        buttback.setOnClickListener{ //закрытие фрагмента
            activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
        }
    }

    override fun onDetach() {
        super.onDetach()
        fragClose.onFragClose(adapter.list)
    }

}