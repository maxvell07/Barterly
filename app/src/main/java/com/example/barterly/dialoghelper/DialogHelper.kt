package com.example.barterly.dialoghelper

import androidx.appcompat.app.AlertDialog
import com.example.barterly.MainActivity
import com.example.barterly.R
import com.example.barterly.accounthelper.AccountHelper
import com.example.barterly.databinding.SignDialogBinding

class DialogHelper(private val act: MainActivity)  {

    private val accHelper = AccountHelper(act)

    fun createSignDialog(index:Int){
        val builder = AlertDialog.Builder(act)
        val bindingDialogElement = SignDialogBinding.inflate(act.layoutInflater)
        val view = bindingDialogElement.root
        if (index==DialogConst.SIGN_UP_STATE){
            bindingDialogElement.tvSign.text = act.resources.getString(R.string.sign_up)
            bindingDialogElement.butSignup.text = act.resources.getString(R.string.sign_up_action)
        } else {
            bindingDialogElement.tvSign.text = act.resources.getString(R.string.sign_in)
            bindingDialogElement.butSignup.text = act.resources.getString(R.string.sign_in_action)
        }
        bindingDialogElement.butSignup.setOnClickListener{
            if (index ==DialogConst.SIGN_UP_STATE){
                accHelper.signUpWithEmail(bindingDialogElement.edSignEmail.text.toString(),bindingDialogElement.edSignPassword.text.toString())
            } else {

            }
        }
        builder.setView(view)
        builder.show()
    }
}