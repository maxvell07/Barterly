package com.example.barterly.presentation.dialogs

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.barterly.R
import com.example.barterly.data.model.Offer
import androidx.core.graphics.drawable.toDrawable
import com.example.barterly.presentation.adapters.DialogOfferAdapter

class OfferListDialog(
    private val offers: ArrayList<Offer>,
    private val onOfferSelected: (Offer) -> Unit
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_with_offers_list)

        val recyclerView = dialog.findViewById<RecyclerView>(R.id.recyclerViewOptions)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = DialogOfferAdapter(offers, onOfferSelected)

        dialog.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        return dialog
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}
