package com.example.barterly.presentation.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.barterly.R
import com.example.barterly.data.model.FiltersCriteries
import com.example.barterly.di.BarterlyApp
import com.example.barterly.presentation.dialogs.DialogSpinnerHelper
import com.example.barterly.util.CityHelper

class FilterFragment : Fragment() {

    private lateinit var priceFromInput: EditText
    private lateinit var priceToInput: EditText
    private lateinit var applyButton: Button
    private lateinit var resetButton: Button
    private lateinit var selectCountry: TextView
    private lateinit var selectCity: TextView
    private lateinit var selectCategory: TextView

    private val dialog = DialogSpinnerHelper()

    private val viewModel by lazy {
        (requireActivity().application as BarterlyApp).firebaseViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_filter_dialog, container, false)

        priceFromInput = view.findViewById(R.id.etPriceFrom)
        priceToInput = view.findViewById(R.id.etPriceTo)
        applyButton = view.findViewById(R.id.btnApply)
        resetButton = view.findViewById(R.id.btnReset)
        selectCountry = view.findViewById(R.id.select_country)
        selectCity = view.findViewById(R.id.select_city)
        selectCategory = view.findViewById(R.id.select_category)

        selectCountry.setOnClickListener { onClickSelectCountry() }
        selectCity.setOnClickListener { onClickSelectCity() }
        selectCategory.setOnClickListener { onClickSelectCategory() }

        // Подгружаем сохранённые фильтры
        viewModel.filterslivedata.observe(viewLifecycleOwner) { filters ->
            if (filters != null) checkSave(filters)
        }

        applyButton.setOnClickListener {
            val filters = FiltersCriteries(
                category = getFilterValue(selectCategory, R.string.select_category),
                country = getFilterValue(selectCountry, R.string.select_country),
                city = getFilterValue(selectCity, R.string.select_city),
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

    private fun getFilterValue(textView: TextView, emptyValueResId: Int): String? {
        val text = textView.text.toString().trim()
        val emptyValue = getString(emptyValueResId).trim()
        return if (text.equals(emptyValue, ignoreCase = true)) null else text
    }

    private fun onClickSelectCountry() {
        selectCity.text = getString(R.string.select_city) // сбрасываем город при смене страны
        val countries = CityHelper.getAllCountries(requireContext())
        dialog.showSpinnerDialog(requireActivity(), countries, selectCountry)
    }

    private fun onClickSelectCity() {
        val country = selectCountry.text.toString()
        if (country != getString(R.string.select_country)) {
            val cities = CityHelper.getAllCities(requireContext(), country)
            dialog.showSpinnerDialog(requireActivity(), cities, selectCity)
        } else {
            Toast.makeText(requireContext(), "Сначала выберите страну", Toast.LENGTH_SHORT).show()
        }
    }

    private fun onClickSelectCategory() {
        val categories = resources.getStringArray(R.array.category).toMutableList() as ArrayList<String>
        dialog.showSpinnerDialog(requireActivity(), categories, selectCategory)
    }

    private fun checkSave(saveFilter: FiltersCriteries) {
        saveFilter.country?.let { selectCountry.text = it } ?: run { selectCountry.text = getString(R.string.select_country) }
        saveFilter.city?.let { selectCity.text = it } ?: run { selectCity.text = getString(R.string.select_city) }
        saveFilter.category?.let { selectCategory.text = it } ?: run { selectCategory.text = getString(R.string.select_category) }
        saveFilter.priceFrom?.let { priceFromInput.setText(it.toString()) } ?: run { priceFromInput.text.clear() }
        saveFilter.priceTo?.let { priceToInput.setText(it.toString()) } ?: run { priceToInput.text.clear() }
    }

    companion object {
        fun newInstance(): FilterFragment = FilterFragment()
    }
}