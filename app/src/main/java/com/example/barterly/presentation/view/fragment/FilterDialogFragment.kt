package com.example.barterly.presentation.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import com.example.barterly.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class FilterDialogFragment(
    private val onApplyFilter: (category: String?, city: String?, country: String?, priceFrom: Int?, priceTo: Int?, sortByTime: Boolean) -> Unit
) : BottomSheetDialogFragment() {

    private lateinit var categoryInput: EditText
    private lateinit var cityInput: EditText
    private lateinit var countryInput: EditText
    private lateinit var priceFromInput: EditText
    private lateinit var priceToInput: EditText
    private lateinit var sortByTimeCheckbox: CheckBox
    private lateinit var applyButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_filter_dialog, container, false)

        categoryInput = view.findViewById(R.id.etCategory)
        cityInput = view.findViewById(R.id.etCity)
        countryInput = view.findViewById(R.id.etCountry)
        priceFromInput = view.findViewById(R.id.etPriceFrom)
        priceToInput = view.findViewById(R.id.etPriceTo)
        sortByTimeCheckbox = view.findViewById(R.id.cbSortByTime)
        applyButton = view.findViewById(R.id.btnApply)

        applyButton.setOnClickListener {
            val category = categoryInput.text.toString().ifBlank { null }
            val city = cityInput.text.toString().ifBlank { null }
            val country = countryInput.text.toString().ifBlank { null }
            val priceFrom = priceFromInput.text.toString().takeIf { it.isNotBlank() }?.toIntOrNull()
            val priceTo = priceToInput.text.toString().takeIf { it.isNotBlank() }?.toIntOrNull()
            val sortByTime = sortByTimeCheckbox.isChecked

            // Передаем данные в функцию обработки фильтрации
            onApplyFilter(category, city, country, priceFrom, priceTo, sortByTime)
            dismiss()
        }

        return view
    }
}

