package com.example.barterly.presentation.view.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.barterly.data.model.Offer
import com.example.barterly.databinding.FragmentHomeBinding
import com.example.barterly.di.BarterlyApp
import com.example.barterly.presentation.adapters.OfferListener
import com.example.barterly.presentation.adapters.OffersRcAdapter
import com.example.barterly.presentation.view.act.DescriptionAct
import com.example.barterly.presentation.view.act.EditOfferAct
import com.example.barterly.presentation.view.act.MainActivity
import com.example.barterly.presentation.viewmodel.OfferListType
import android.content.Context
import android.net.ConnectivityManager

class MyOffersFragment : Fragment(), OfferListener {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: OffersRcAdapter
    private val viewModel by lazy {
        (requireActivity().application as BarterlyApp).firebaseViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = OffersRcAdapter(this)
        binding.rcView.layoutManager = LinearLayoutManager(requireContext())
        binding.rcView.adapter = adapter

        viewModel.setCurrentType(OfferListType.MY)
        viewModel.loadMyOffers()

        viewModel.myOffersData.observe(viewLifecycleOwner) { list ->
            val safeList = list ?: emptyList()
            adapter.updateAdapter(safeList)
            binding.tvEmpty.visibility = if (safeList.isEmpty()) View.VISIBLE else View.GONE
        }
        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            binding.progress.visibility = if (loading) View.VISIBLE else View.GONE
        }
    }
    override fun onResume() {
        super.onResume()
        viewModel.loadMyOffers()  // или другой метод загрузки для конкретного фрагмента
    }


    override fun onFavClick(offer: Offer) {
        viewModel.onFavClick(offer)
    }

    override fun onOfferViewed(offer: Offer) {
        if (!isNetworkAvailable()) {
            (requireActivity() as MainActivity).showNoInternetFragment()
            return
        }
        val intent = Intent(requireContext(), DescriptionAct::class.java).apply {
            putExtra(MainActivity.OFFER_KEY, offer.key)
        }
        viewModel.offerViewed(offer)
        startActivity(intent)
    }

    override fun onDeleteOffer(offer: Offer) {
        viewModel.deleteoffer(offer)
    }

    override fun onEditOffer(offer: Offer) {
        if (!isNetworkAvailable()) {
            (requireActivity() as MainActivity).showNoInternetFragment()
            return
        }
        val intent = Intent(requireContext(), EditOfferAct::class.java).apply {
            putExtra(MainActivity.EDIT_STATE, true)
            putExtra(MainActivity.OFFER_KEY, offer.key)
        }
        startActivity(intent)
    }

    private fun isNetworkAvailable(): Boolean {
        val cm = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo?.isConnectedOrConnecting == true
    }
}
