package com.example.barterly.dialoghelper

import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.barterly.act.MainActivity
import com.example.barterly.R
import com.example.barterly.accounthelper.AccountHelper
import com.example.barterly.databinding.SignDialogBinding

class DialogHelper(private val act: MainActivity)  {

     val accHelper = AccountHelper(act)

    fun createSignDialog(index:Int){
        val builder = AlertDialog.Builder(act)
        val bindingDialogElement = SignDialogBinding.inflate(act.layoutInflater)
        val view = bindingDialogElement.root
        builder.setView(view)

        setDialogState(index, bindingDialogElement)

        val dialog = builder.create()

        bindingDialogElement.butGoogleSignup.setOnClickListener{
            accHelper.signInWithGoogle()
            dialog.dismiss()
        }
        bindingDialogElement.butSignup.setOnClickListener{
        setOnClickSignUpIn(index,bindingDialogElement,dialog)
        }

        bindingDialogElement.butForgetPas.setOnClickListener{
            setOnClickResetPassword(bindingDialogElement,dialog)
        }

        dialog.show()
    }

    private fun setOnClickResetPassword(bindingDialogElement: SignDialogBinding, dialog: AlertDialog) {
        if (bindingDialogElement.edSignEmail.text.isNotEmpty()){
            act.myAuth.sendPasswordResetEmail(bindingDialogElement.edSignEmail.text.toString()).addOnCompleteListener{ task ->
                if (task.isSuccessful){
                    Toast.makeText(act,R.string.email_reset_password_was_sent,Toast.LENGTH_LONG).show()
                }
            }
            dialog.dismiss()
        } else{
            bindingDialogElement.tvDialogMessage.visibility = View.VISIBLE
        }
    }

    private fun setOnClickSignUpIn(index: Int, bindingDialogElement: SignDialogBinding,dialog: AlertDialog) {
            dialog.dismiss()
            if (index ==DialogConst.SIGN_UP_STATE){
                accHelper.signUpWithEmail(bindingDialogElement.edSignEmail.text.toString(),bindingDialogElement.edSignPassword.text.toString())
            } else {
                accHelper.signInWithEmail(bindingDialogElement.edSignEmail.text.toString(),bindingDialogElement.edSignPassword.text.toString())
            }
        }

    private fun setDialogState(index: Int, bindingDialogElement: SignDialogBinding) {
        if (index==DialogConst.SIGN_UP_STATE){
            bindingDialogElement.tvSign.text = act.resources.getString(R.string.sign_up)
            bindingDialogElement.butSignup.text = act.resources.getString(R.string.sign_up_action)
        } else {
            bindingDialogElement.tvSign.text = act.resources.getString(R.string.sign_in)
            bindingDialogElement.butSignup.text = act.resources.getString(R.string.sign_in_action)
            bindingDialogElement.butForgetPas.visibility = View.VISIBLE
        }
    }


}