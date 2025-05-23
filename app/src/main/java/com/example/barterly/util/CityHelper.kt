package com.example.barterly.util

import android.content.Context
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.util.Locale

object CityHelper {
    fun getAllCountries(context: Context): ArrayList<String> {
        var arr = ArrayList<String>()
        try {
            val inputStream: InputStream = context.assets.open("countriesToCities.json")
            val size: Int = inputStream.available()// узнаем размер
            val byteSArray = ByteArray(size)
            inputStream.read(byteSArray)
            val jsonFile = String(byteSArray)
            val jsonObject = JSONObject(jsonFile)
            val countryName = jsonObject.names()
            if (countryName != null) {
                for (i in 0 until countryName.length()) {
                    arr.add(countryName.getString(i))
                }
            }

        } catch (e: IOException) {

        }
        return arr
    }

    fun getAllCities(context: Context, country:String): ArrayList<String> {
        var arr = ArrayList<String>()
        try {
            val inputStream: InputStream = context.assets.open("countriesToCities.json")
            val size: Int = inputStream.available()// узнаем размер
            val byteSArray = ByteArray(size)
            inputStream.read(byteSArray)
            val jsonFile = String(byteSArray)
            val jsonObject = JSONObject(jsonFile)
            val cityNames = jsonObject.getJSONArray(country)
            for (i in 0 until cityNames.length()) {
                arr.add(cityNames.getString(i))
            }

        } catch (e: IOException) {

        }
        return arr
    }

    fun filterListData(list: ArrayList<String>, text: String?): ArrayList<String> {
        val filterlist = ArrayList<String>()
        filterlist.clear()
        if (text != null) {
            for (Selection: String in list) {
                if (Selection.lowercase(Locale.ROOT).startsWith(text.lowercase(Locale.ROOT))) {
                    filterlist.add(Selection)
                }
            }
        }
        if (filterlist.size == 0) {
            filterlist.add("No result")
        }

        return filterlist
    }

}