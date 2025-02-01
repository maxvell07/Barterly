package com.example.barterly.dialoghelper

import android.app.Activity
import androidx.appcompat.app.AlertDialog
import com.example.barterly.databinding.ProgressDialogLayoutBinding
import com.example.barterly.databinding.SignDialogBinding

object ProgressDialog {



    fun createLoadingDialog(act:Activity):AlertDialog{
        val builder = AlertDialog.Builder(act)
        val bindingDialogElement = ProgressDialogLayoutBinding.inflate(act.layoutInflater)
        val view = bindingDialogElement.root
        builder.setView(view)
        val dialog = builder.create()

        dialog.setCancelable(false)//чтобы пользователь не мог закрыть диалог загрузки
        dialog.show()
        return dialog
    }

}