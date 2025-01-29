package com.example.barterly.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.barterly.R
import com.example.barterly.databinding.ListImageFragBinding
import com.example.barterly.utils.ImagePiker
import com.example.barterly.utils.ItemTouchMoveCallback

class ImageListFragment(val fragClose:FragmentCloseInterface, private  val newlist: ArrayList<String>) : Fragment() {

    lateinit var binding: ListImageFragBinding
    val adapter = SelectImageAdapter()
    val dragcallback = ItemTouchMoveCallback(adapter)
    val touchHelper = ItemTouchHelper(dragcallback)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ListImageFragBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpToolBar()
        touchHelper.attachToRecyclerView(binding.rcvSelectImage)
        binding.rcvSelectImage.layoutManager = LinearLayoutManager(activity)
        binding.rcvSelectImage.adapter =adapter

        adapter.updateAdapter(newlist,true)

    }

    override fun onDetach() {
        super.onDetach()
        fragClose.onFragClose(adapter.list)
    }
    private fun setUpToolBar(){
        binding.toolbarfrag.inflateMenu(R.menu.menu_imagefrag)
        val deleteitem = binding.toolbarfrag.menu.findItem(R.id.delete_image)
        val additem = binding.toolbarfrag.menu.findItem(R.id.add_image)

        binding.toolbarfrag.setNavigationOnClickListener{
            activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
            Toast.makeText(binding.root.context, "cancel",Toast.LENGTH_SHORT).show()
        }
        deleteitem.setOnMenuItemClickListener {
            Toast.makeText(binding.root.context, "text2",Toast.LENGTH_SHORT).show()
            adapter.updateAdapter(ArrayList(),true)
            true
        }
        additem.setOnMenuItemClickListener {
            if (adapter.itemCount < 3) {
                Toast.makeText(binding.root.context, "text3", Toast.LENGTH_SHORT).show()
                ImagePiker.getImages(
                    activity as AppCompatActivity,
                    ImagePiker.MAX_IMAGE_COUNT - adapter.list.size,ImagePiker.REQUEST_CODE_GET_IMAGES
                )
            }
                true
        }

    }
    fun updateAdapter(newlist:ArrayList<String>){

        adapter.updateAdapter(newlist,false)

    }
    fun selectsingleImage(uri:String,pos:Int){
        adapter.list[pos] = uri
        adapter.notifyDataSetChanged()

    }


}