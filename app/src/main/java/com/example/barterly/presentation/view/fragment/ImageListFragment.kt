package com.example.barterly.presentation.view.fragment

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
import com.example.barterly.presentation.view.act.EditOfferAct
import com.example.barterly.databinding.ListImageFragBinding
import com.example.barterly.util.dialoghelper.ProgressDialog
import com.example.barterly.util.AdapterDeleteCallback
import com.example.barterly.util.ImageManager
import com.example.barterly.util.ImagePiker
import com.example.barterly.util.ItemTouchMoveCallback
import kotlinx.coroutines.*

class ImageListFragment(private val fragClose: FragmentCloseInterface) : Fragment(), AdapterDeleteCallback {

    private var _binding: ListImageFragBinding? = null
    private val binding get() = _binding!!

    private val adapter = SelectedImageRcvAdapter(this)
    private val touchHelper = ItemTouchHelper(ItemTouchMoveCallback(adapter))

    private var addItem: MenuItem? = null
    private var job: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ListImageFragBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpToolBar()
        touchHelper.attachToRecyclerView(binding.rcvSelectImage)
        binding.rcvSelectImage.layoutManager = LinearLayoutManager(requireContext())
        binding.rcvSelectImage.adapter = adapter
    }

    fun updateAdapterFromEdit(bitmapList: List<Bitmap>) {
        adapter.updateAdapter(bitmapList, true)
    }

    override fun onClickDel() {
        addItem?.isVisible = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rcvSelectImage.adapter = null // Освобождаем RecyclerView
        _binding = null // Освобождаем память
        job?.cancel() // Отменяем корутину
    }

    override fun onDetach() {
        super.onDetach()
        fragClose.onFragClose(adapter.list)
        job?.cancel()
    }

    fun resizeSelectedImages(newList: ArrayList<Uri>, needClear: Boolean, activity: Activity) {
        job = CoroutineScope(Dispatchers.Main).launch {
            val dialog = ProgressDialog.createLoadingDialog(activity)
            val bitmapList = withContext(Dispatchers.IO) {
                ImageManager.imageResize(newList, activity)
            }
            dialog.dismiss()
            adapter.updateAdapter(bitmapList, needClear)
            if (adapter.list.size == 3) addItem?.isVisible = false
        }
    }

    private fun setUpToolBar() {
        binding.toolbarfrag.inflateMenu(R.menu.menu_imagefrag)
        val deleteItem = binding.toolbarfrag.menu.findItem(R.id.delete_image)
        addItem = binding.toolbarfrag.menu.findItem(R.id.add_image)
        addItem?.isVisible = adapter.list.size < 3

        binding.toolbarfrag.setNavigationOnClickListener {
            parentFragmentManager.beginTransaction().remove(this).commit()
            Toast.makeText(requireContext(), "cancel", Toast.LENGTH_SHORT).show()
        }

        deleteItem.setOnMenuItemClickListener {
            adapter.updateAdapter(ArrayList(), true)
            addItem?.isVisible = true
            true
        }

        addItem?.setOnMenuItemClickListener {
            val imageCounter = ImagePiker.MAX_IMAGE_COUNT - adapter.list.size
            ImagePiker.pickImages(activity as EditOfferAct, imageCounter)
            true
        }
    }

    fun updateAdapter(newList: ArrayList<Uri>, activity: Activity) {
        resizeSelectedImages(newList, false, activity)
    }

    fun selectSingleImage(uri: Uri, pos: Int) {
        val loadBar = binding.rcvSelectImage[pos].findViewById<ProgressBar>(R.id.loadbarimage)

        job = CoroutineScope(Dispatchers.Main).launch {
            loadBar.visibility = View.VISIBLE
            val bitmapList = withContext(Dispatchers.IO) {
                ImageManager.imageResize(arrayListOf(uri), activity as Activity)
            }
            loadBar.visibility = View.GONE
            adapter.list[pos] = bitmapList[0]
            adapter.notifyItemChanged(pos)
        }
    }
}
