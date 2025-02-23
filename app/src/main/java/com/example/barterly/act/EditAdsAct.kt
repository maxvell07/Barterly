package com.example.barterly.act

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.barterly.R
import com.example.barterly.adapters.ImageAdapter
import com.example.barterly.model.Offer
import com.example.barterly.model.DbManager
import com.example.barterly.databinding.ActivityEditAdsBinding
import com.example.barterly.dialogs.DialogSpinnerHelper
import com.example.barterly.fragment.FragmentCloseInterface
import com.example.barterly.fragment.ImageListFragment
import com.example.barterly.model.finishLoadListener
import com.example.barterly.utils.CityHelper
import com.example.barterly.utils.ImagePiker


class EditAdsAct : AppCompatActivity(), FragmentCloseInterface {
    lateinit var binding: ActivityEditAdsBinding
    private val dialog = DialogSpinnerHelper()
    private var ispermisssionGranted = false
    lateinit var imageViewAdapter: ImageAdapter
    var chooseImageFrag: ImageListFragment? = null
    private val dbmanager = DbManager()
    var editimagepos = 0
    private var iseditstate = false
    private var offer:Offer? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditAdsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        checkeditstate()
    }
    private fun checkeditstate(){
        iseditstate = iseditstate()
        if (iseditstate){
            offer = intent.getSerializableExtra(MainActivity.OFFER_DATA) as Offer
            if (offer != null) {fillViews(offer!!)}
        }
    }

    private fun iseditstate():Boolean{
        return intent.getBooleanExtra(MainActivity.EDIT_STATE,false) //проверяем у интента открывшего true или false
    }
    private fun fillViews(offer: Offer) = with(binding){ // заполняем оффер при редактировании
        selectCountry.text = offer.country
        selectCity.text = offer.city
        editTitleOffer.setText(offer.title)
        adresseditText.setText(offer.adress)
        phoneEditText.setText(offer.phone)
        selectCategory.setText(offer.category)
        editTextdiscription.setText(offer.description)
        priceeditrext.setText(offer.price)

    }

    private fun init() {

        imageViewAdapter = ImageAdapter()
        binding.vpImages.adapter = imageViewAdapter
    }

    //OnClicks
    fun onClickSelectCountry(view: View) {
        if (binding.selectCity.text != getString(R.string.select_city)) {
            binding.selectCity.text = getString(R.string.select_city)
        }
        val listCountry = CityHelper.getAllCountries(this)
        dialog.showSpinnerDialog(this, listCountry, binding.selectCountry)

    }

    fun onClickSelectCity(view: View) {
        var city = binding.selectCountry.text.toString()
        if (city != view.context.getString(R.string.select_country)) {
            val listCity = CityHelper.getAllCities(this, city)
            dialog.showSpinnerDialog(this, listCity, binding.selectCity)
        } else {
            Toast.makeText(this, "No country selected", Toast.LENGTH_LONG).show()
        }
    }
    fun onClickSelectCat(view:View){

        val listCity = resources.getStringArray(R.array.category).toMutableList() as ArrayList
        dialog.showSpinnerDialog(this, listCity, binding.selectCategory)

    }

    fun onClickGetImages(view: View) {
        if (imageViewAdapter.array.size == 0){

            ImagePiker.pickSeveralImages(this,3)

        } else {
            openChoosenImageFrag(null)
            chooseImageFrag?.updateAdapterFromEdit(imageViewAdapter.array)
        }
    }

    fun onClickPublish(view:View){
        val offertemp =  filloffer()
        if (iseditstate){
        dbmanager.publishOffer(offertemp.copy(key = offer?.key),onPublishFinish())

        }
        else {
            dbmanager.publishOffer(offertemp,onPublishFinish())
        }

        // отправка на сервер картинок

    }
    private fun onPublishFinish():finishLoadListener{
        return object: finishLoadListener{
            override fun onFinish(Bol:Boolean) {
                finish()
            }
        }
    }

    fun filloffer():Offer{
        val offer:Offer
        binding.apply {
            offer = Offer(
                binding.editTitleOffer.text.toString(),
                binding.selectCountry.text.toString(),
                binding.selectCity.text.toString(),
                binding.phoneEditText.text.toString(),
                binding.adresseditText.text.toString(),
                binding.selectCategory.text.toString(),
                binding.priceeditrext.text.toString(),
                binding.editTextdiscription.text.toString(),
                dbmanager.db.push().key,// генерируем уникальный ключ офера
                dbmanager.auth.uid
            )
        }
        return offer
    }

    override fun onFragClose(list: ArrayList<Bitmap>) {
        binding.scrolview.visibility = View.VISIBLE
        imageViewAdapter.update(list)
        chooseImageFrag = null
    }

    fun openChoosenImageFrag(newlist:ArrayList<Uri>?){
        chooseImageFrag = ImageListFragment(this)
        if (newlist != null) chooseImageFrag?.resizeSelectedImages(newlist,true,this)
        binding.scrolview.visibility = View.GONE
        supportFragmentManager.beginTransaction()
            .replace(R.id.placeholder, chooseImageFrag!!)
            .commit()
    }
    private fun loadpictures(adapter: ImageAdapter){

    }
}