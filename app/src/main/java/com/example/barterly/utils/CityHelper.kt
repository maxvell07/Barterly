package com.example.barterly.utils

import android.content.Context
import org.json.JSONObject
import java.io.IOError
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

object CityHelper {
    fun getAllCountries(context: Context):ArrayList<String>{
        var arr = ArrayList<String>()
        try {
            val inputStream: InputStream = context.assets.open("countriesToCities.json")
            val size:Int = inputStream.available()// узнаем размер
            val byteSArray = ByteArray(size)
            inputStream.read(byteSArray)
            val jsonFile = String(byteSArray)
            val jsonObject = JSONObject(jsonFile)
            val countryName = jsonObject.names()
            for (i in 0 until countryName.length()){
                arr.add(countryName.getString(i))
            }

        }catch (e:IOException){

        }
    return arr
    }
}