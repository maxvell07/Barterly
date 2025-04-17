package com.example.barterly.util.dialogs

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.DialogFragment
import com.example.barterly.R

class BuyOrTradeDialog(
    private val onBuyClicked: () -> Unit,
    private val onTradeClicked: () -> Unit
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_trade_or_buy)

        dialog.findViewById<Button>(R.id.btnBuy).setOnClickListener {
            onBuyClicked()
            dialog.dismiss()
        }

        dialog.findViewById<Button>(R.id.btnTrade).setOnClickListener {
            onTradeClicked()
            dialog.dismiss()
        }

        dialog.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        return dialog
    }

    override fun onStart() {
        super.onStart()
        // Устанавливаем ширину окна диалога
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}