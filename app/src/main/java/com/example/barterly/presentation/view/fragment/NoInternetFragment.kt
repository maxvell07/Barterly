package com.example.barterly.presentation.view.fragment

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.barterly.R

class NoInternetFragment : Fragment() {

    interface RetryListener {
        fun onRetrySuccess()
    }

    private var retryListener: RetryListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        retryListener = context as? RetryListener
    }

    override fun onDetach() {
        super.onDetach()
        retryListener = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_error_connection, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val btnRetry = view.findViewById<Button>(R.id.btnRetry)

        btnRetry.setOnClickListener {
            if (isNetworkAvailable(requireContext())) {
                retryListener?.onRetrySuccess()
            } else {
                Toast.makeText(context, "Нет подключения", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo?.isConnectedOrConnecting == true
    }
}

