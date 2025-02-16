package com.example.barterly.fragment

import android.app.Activity
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.barterly.R
import com.example.barterly.act.EditAdsAct
import com.example.barterly.databinding.ListImageFragBinding
import com.example.barterly.dialoghelper.ProgressDialog
import com.example.barterly.utils.AdapterDeleteCallback
import com.example.barterly.utils.ImageManager
import com.example.barterly.utils.ImagePiker
import com.example.barterly.utils.ItemTouchMoveCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ImageListFragment(val fragClose:FragmentCloseInterface) : Fragment(),AdapterDeleteCallback {

    lateinit var binding: ListImageFragBinding
    val adapter = SelectedImageRcvAdapter(this)
    val dragcallback = ItemTouchMoveCallback(adapter)
    val touchHelper = ItemTouchHelper(dragcallback)
    private var additem:MenuItem? = null
    private var job: Job? = null
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
    }

    fun updateAdapterFromEdit(bitmaplist: List<Bitmap>){
        adapter.updateAdapter(bitmaplist,true)
    }

    override fun onClickDel() {
        additem?.isVisible = true
    }

    override fun onDetach() {
        super.onDetach()
        fragClose.onFragClose(adapter.list)
        job?.cancel()
    }

    fun resizeSelectedImages(newlist:ArrayList<Uri>,needclear :Boolean,activity: Activity){

        job = CoroutineScope(Dispatchers.Main).launch {
            val dialog = ProgressDialog.createLoadingDialog(activity)
            val bitmaplist = ImageManager.imageResize(newlist, activity)
            dialog.dismiss()
            adapter.updateAdapter(bitmaplist,needclear)
            if (adapter.list.size == 3) additem?.isVisible = false
        }

    }
    private fun setUpToolBar(){
        binding.toolbarfrag.inflateMenu(R.menu.menu_imagefrag)
        val deleteitem = binding.toolbarfrag.menu.findItem(R.id.delete_image)
        additem = binding.toolbarfrag.menu.findItem(R.id.add_image)
        if (adapter.list.size == 3) additem?.isVisible = false

        binding.toolbarfrag.setNavigationOnClickListener{
            activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
            Toast.makeText(binding.root.context, "cancel",Toast.LENGTH_SHORT).show()
        }
        deleteitem.setOnMenuItemClickListener {
            adapter.updateAdapter(ArrayList(),true)
            additem?.isVisible = true
            true
        }
        additem?.setOnMenuItemClickListener {
            val imageCounter = ImagePiker.MAX_IMAGE_COUNT - adapter.list.size
            ImagePiker.pickImages(activity as EditAdsAct,imageCounter )
            true
        }

    }
    fun updateAdapter(newlist:ArrayList<Uri>,activity:Activity){

        resizeSelectedImages(newlist,false,activity)

    }
    fun selectsingleImage(uri:Uri,pos:Int){

        val loadbar = binding.rcvSelectImage[pos].findViewById<ProgressBar>(R.id.loadbarimage)
        job = CoroutineScope(Dispatchers.Main).launch {
            loadbar.visibility = View.VISIBLE
            val bitmaplist = ImageManager.imageResize(arrayListOf(uri),activity as Activity)
            loadbar.visibility = View.GONE
            adapter.list[pos] = bitmaplist[0]
            adapter.notifyItemChanged(pos)
        }
    }
}