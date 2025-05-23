package com.example.barterly.presentation.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.example.barterly.R
import com.example.barterly.data.model.FiltersCriteries
import com.example.barterly.di.BarterlyApp

class FilterFragment : Fragment() {

    private lateinit var categoryInput: EditText
    private lateinit var cityInput: EditText
    private lateinit var countryInput: EditText
    private lateinit var priceFromInput: EditText
    private lateinit var priceToInput: EditText
    private lateinit var applyButton: Button
    private lateinit var resetButton: Button


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_filter_dialog, container, false)

        val viewModel = (requireActivity().application as BarterlyApp).firebaseViewModel

        categoryInput = view.findViewById(R.id.etCategory)
        cityInput = view.findViewById(R.id.etCity)
        countryInput = view.findViewById(R.id.etCountry)
        priceFromInput = view.findViewById(R.id.etPriceFrom)
        priceToInput = view.findViewById(R.id.etPriceTo)
        applyButton = view.findViewById(R.id.btnApply)
        resetButton = view.findViewById(R.id.btnReset)

        applyButton.setOnClickListener {
            val filters = FiltersCriteries(
                category = categoryInput.text.toString().ifBlank { null },
                city = cityInput.text.toString().ifBlank { null },
                country = countryInput.text.toString().ifBlank { null },
                priceFrom = priceFromInput.text.toString().toIntOrNull(),
                priceTo = priceToInput.text.toString().toIntOrNull()
            )
            viewModel.applyFilters(filters)
            parentFragmentManager.popBackStack()
        }

        resetButton.setOnClickListener {
            viewModel.clearFilters()
            parentFragmentManager.popBackStack()
        }

        return view
    }

    companion object {
        fun newInstance(): FilterFragment = FilterFragment()
    }
}
