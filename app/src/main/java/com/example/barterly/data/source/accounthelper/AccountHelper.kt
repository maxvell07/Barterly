package com.example.barterly.data.source.accounthelper

import android.widget.Toast
import com.example.barterly.presentation.view.act.MainActivity
import com.example.barterly.R
import com.example.barterly.constants.FirebaseAuthConstants
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class AccountHelper(private val act: MainActivity) {

    private lateinit var signInClient: GoogleSignInClient
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun signUpWithEmail(email: String, password: String) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        task.result?.user?.let {
                            sendEmailVerification(it)
                            act.uiUpdate(it)
                        }
                    } else {
                        signUpWithEmailExceptions(task.exception!!, email, password)
                    }
                }
        }
    }

    private fun signUpWithEmailExceptions(e: Exception, email: String, password: String) {
        when (e) {
            is FirebaseAuthUserCollisionException -> {
                if (e.errorCode == FirebaseAuthConstants.ERROR_EMAIL_ALREADY_IN_USE) {
                    Toast.makeText(act, R.string.error_email_already_in_use, Toast.LENGTH_LONG).show()
                    linkEmailToGoogle(email, password)
                }
            }

            is FirebaseAuthInvalidCredentialsException -> {
                if (e.errorCode == FirebaseAuthConstants.ERROR_INVALID_EMAIL) {
                    Toast.makeText(act, R.string.error_invalid_email, Toast.LENGTH_LONG).show()
                }
            }

            is FirebaseAuthWeakPasswordException -> {
                Toast.makeText(act, R.string.error_weak_password, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun linkEmailToGoogle(email: String, password: String) {
        val credential = EmailAuthProvider.getCredential(email, password)
        auth.currentUser?.linkWithCredential(credential)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(act, R.string.link_email_done, Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(act, R.string.link_email_failed, Toast.LENGTH_LONG).show()
            }
        } ?: Toast.makeText(act, R.string.enter_to_g, Toast.LENGTH_LONG).show()
    }

    fun signInWithEmail(email: String, password: String) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        act.uiUpdate(task.result?.user)
                    } else {
                        signInWithEmailExceptions(task.exception!!)
                    }
                }
        }
    }

    private fun signInWithEmailExceptions(e: Exception) {
        when (e) {
            is FirebaseAuthInvalidCredentialsException -> {
                if (e.errorCode == FirebaseAuthConstants.ERROR_INVALID_EMAIL) {
                    Toast.makeText(act, R.string.error_invalid_email, Toast.LENGTH_LONG).show()
                } else if (e.errorCode == FirebaseAuthConstants.ERROR_WRONG_PASSWORD) {
                    Toast.makeText(act, R.string.error_wrong_password, Toast.LENGTH_LONG).show()
                }
            }

            is FirebaseAuthInvalidUserException -> {
                if (e.errorCode == FirebaseAuthConstants.ERROR_USER_NOT_FOUND) {
                    Toast.makeText(act, R.string.error_user_not_found, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    fun signInFirebaseWithGoogle(token: String) {
        val credential = GoogleAuthProvider.getCredential(token, null)
        auth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(act, "Sign in done", Toast.LENGTH_LONG).show()
                act.uiUpdate(task.result?.user)
            } else {
                Toast.makeText(act, "Google Sign In failed", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun signOutGoogle() {
        getSignInClient().signOut()
    }

    fun signInWithGoogle() {
        signInClient = getSignInClient()
        val intent = signInClient.signInIntent
        act.googleSignInLauncher.launch(intent)
    }

    private fun getSignInClient(): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(act.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(act, gso)
    }

    private fun sendEmailVerification(user: FirebaseUser) {
        user.sendEmailVerification().addOnCompleteListener { task ->
            val message = if (task.isSuccessful) {
                R.string.send_verification_email_done
            } else {
                R.string.send_verification_email_error
            }
            Toast.makeText(act, act.getString(message), Toast.LENGTH_LONG).show()
        }
    }

    fun signInAnonymously(listener: Listener) {
        auth.signInAnonymously().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                listener.onComplete()
                Toast.makeText(act, "Signed in anonymously", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(act, "Anonymous sign in failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    interface Listener {
        fun onComplete()
    }
}