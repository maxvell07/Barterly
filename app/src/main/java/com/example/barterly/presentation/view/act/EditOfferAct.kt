package com.example.barterly.presentation.view.act

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.barterly.di.BarterlyApp
import com.example.barterly.R
import com.example.barterly.presentation.adapters.ImageAdapter
import com.example.barterly.data.model.Offer
import com.example.barterly.data.model.DbManager
import com.example.barterly.databinding.ActivityEditAdsBinding
import com.example.barterly.presentation.dialogs.DialogSpinnerHelper
import com.example.barterly.presentation.view.fragment.FragmentCloseInterface
import com.example.barterly.presentation.view.fragment.ImageListFragment
import com.example.barterly.data.model.finishLoadListener
import com.example.barterly.util.CityHelper
import com.example.barterly.util.ImagePiker
import com.example.barterly.presentation.viewmodel.FirebaseViewModel
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class EditOfferAct : AppCompatActivity(), FragmentCloseInterface {
    lateinit var binding: ActivityEditAdsBinding
    private val dialog = DialogSpinnerHelper()
    lateinit var imageViewAdapter: ImageAdapter
    var chooseImageFrag: ImageListFragment? = null
    private val dbmanager = DbManager()
    var editimagepos = 0
    private var iseditstate = false
    private var offer: Offer? = null
    private lateinit var titleEdit: EditText
    private lateinit var phoneEdit: EditText
    private lateinit var addressEdit: EditText
    private lateinit var priceEdit: EditText
    private lateinit var descriptionEdit: EditText
    private lateinit var firebaseViewModel: FirebaseViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditAdsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseViewModel = (application as BarterlyApp).firebaseViewModel
        titleEdit = findViewById(R.id.edit_title_offer)
        phoneEdit = findViewById(R.id.phone_edit_text)
        addressEdit = findViewById(R.id.adresseditText)
        priceEdit = findViewById(R.id.priceeditrext)
        descriptionEdit = findViewById(R.id.editTextdiscription)
        init()
        checkeditstate()
        updateImageCounter()
        findViewById<android.widget.Button>(R.id.button).setOnClickListener {
            if (validateFields()) {
                onClickPublish()
            }
        }
    }
    private fun validateFields(): Boolean {
        var isValid = true

        // Список обязательных EditText
        val editFields = listOf(
            R.id.edit_title_offer,
            R.id.phone_edit_text,
            R.id.adresseditText,
            R.id.priceeditrext,
            R.id.editTextdiscription
        )

        // Проверяем EditText: если пустой — делаем hint красным
        editFields.forEach { id ->
            val editText = findViewById<EditText>(id)
            if (editText.text.isNullOrBlank()) {
                editText.setHintTextColor(Color.RED)
                isValid = false
            }
        }

        val selectViews = listOf(
            binding.selectCategory to getString(R.string.select_category),
            binding.selectCountry to getString(R.string.select_country),
            binding.selectCity to getString(R.string.select_city)
        )

        selectViews.forEach { (textView, defaultText) ->
            if (textView.text.toString() == defaultText) {
                isValid = false
            }
        }
        return isValid
    }

    private fun onClickPublish() {
        // Загрузка и публикация объявления
        firebaseViewModel.deleteAllImages(offer?.key.toString())
        val offertemp = filloffer()
        if (iseditstate) {
            uploadImagesAndDelete(offertemp.key.toString())
            dbmanager.publishOffer(offertemp, onPublishFinish())
        } else {
            uploadImagesAndDelete(offertemp.key.toString())
            dbmanager.publishOffer(offertemp, onPublishFinish())
        }
        firebaseViewModel.loadoffers()
    }
    private fun checkeditstate() {
        iseditstate = iseditstate()
        if (iseditstate) {
            var key = intent.getSerializableExtra(MainActivity.OFFER_KEY) as String
            firebaseViewModel.liveOffersData.observe(this) { offers ->
                offer = offers?.find { it.key == key }
                offer?.let { fillViews(it) }
            }
        }
        Log.d("offer"," offerkey =${offer?.key}")
        Log.d("offer"," offerimg1 =${offer?.img1}")
    }

    private fun iseditstate(): Boolean {
        return intent.getBooleanExtra(
            MainActivity.EDIT_STATE,
            false
        ) //проверяем у интента открывшего true или false
    }

    private fun fillViews(offer: Offer) = with(binding) {
        //observelive data
        // Обновляем поля UI

        selectCountry.text = offer.country
        selectCity.text = offer.city
        editTitleOffer.setText(offer.title)
        adresseditText.setText(offer.adress)
        phoneEditText.setText(offer.phone)
        selectCategory.setText(offer.category)
        editTextdiscription.setText(offer.description)
        priceeditrext.setText(offer.price)

        imageViewAdapter.array.clear()

        val images = ArrayList<String>().apply {
            offer.img1?.let { add(it) }
            offer.img2?.let { add(it) }
            offer.img3?.let { add(it) }
        }

        loadImagesToBitmaps(images) { bitmaps ->
            imageViewAdapter.array.clear()
            imageViewAdapter.array.addAll(bitmaps)
            imageViewAdapter.notifyDataSetChanged()
        }

    }

    private fun loadImagesToBitmaps(urls: List<String>, callback: (List<Bitmap>) -> Unit) {
        val bitmaps = mutableListOf<Bitmap>()
        var loadedCount = 0

        for (url in urls) {
            Picasso.get().load(url).tag(this).into(object : Target {
                override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                    bitmap?.let {
                        bitmaps.add(it)
                    }
                    loadedCount++
                    if (loadedCount == urls.size) {
                        callback(bitmaps)
                    }
                }

                override fun onBitmapFailed(
                    e: Exception?,
                    errorDrawable: Drawable?
                ) {
                    loadedCount++
                    if (loadedCount == urls.size) {
                        callback(bitmaps)
                    }
                }

                override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
            })
        }
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

    fun onClickSelectCat(view: View) {

        val listCity = resources.getStringArray(R.array.category).toMutableList() as ArrayList
        dialog.showSpinnerDialog(this, listCity, binding.selectCategory)

    }

    fun onClickGetImages(view: View) {
        if (imageViewAdapter.array.size == 0) {
            ImagePiker.pickSeveralImages(this, 3)

        } else {
            openChoosenImageFrag(null)
            chooseImageFrag?.updateAdapterFromEdit(imageViewAdapter.array)

        }
    }

    private fun fileNamesList(): List<String> {
        var fileNames: List<String>  = emptyList()
        when (imageViewAdapter.itemCount){
            1 -> fileNames = listOf("img1.jpg")
            2 -> fileNames = listOf("img1.jpg", "img2.jpg")
            3 -> fileNames = listOf("img1.jpg", "img2.jpg","img3.jpg")
        }
        return  fileNames
    }

    private fun uploadImagesAndDelete(offerKey: String) {
        var fileNames = fileNamesList()
        CoroutineScope(Dispatchers.IO).launch {
            val bitmaps = imageViewAdapter.array.toList() // Создаём копию списка перед изменением
            bitmaps.forEachIndexed { index, bitmap ->
                if (index < fileNames.size) {
                    val fileName = fileNames[index]
                    val file = saveBitmapToFile(bitmap, fileName)

                    if (file != null) {
                        val success = firebaseViewModel.filerepository.uploadFile(offerKey, file)
                        if (success.isSuccessful) {
                            file.delete() // Удаляем файл после успешной загрузки
                        }
                    }
                }
            }
        }

    }

    private fun saveBitmapToFile(bitmap: Bitmap, fileName: String): File? {
        return try {
            val file = File(this.cacheDir, fileName)
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, out)
            }
            file
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    private fun onPublishFinish(): finishLoadListener {
        return object : finishLoadListener {
            override fun onFinish(Bol: Boolean) {
                setResult(RESULT_OK) // сообщаем что успешно
                finish()
            }
        }
    }

    override fun onBackPressed() {
        setResult(RESULT_CANCELED)
        super.onBackPressed()
    }



    fun filloffer(): Offer {
        val offertemp: Offer
        binding.apply {
            offertemp = Offer(
                selectCountry.text.toString(),
                selectCity.text.toString(),
                phoneEditText.text.toString(),
                adresseditText.text.toString(),
                selectCategory.text.toString(),
                editTitleOffer.text.toString(),
                priceeditrext.text.toString(),
                editTextdiscription.text.toString(),
                "empty", "empty", "empty", // временно, потом подставим реальные пути
                offer?.key ?: dbmanager.db.push().key,
                "0",
                dbmanager.auth.uid,
                System.currentTimeMillis().toString()
            )
        }

        val imgBasePath = "/images/${offertemp.key}/"
        val count = imageViewAdapter.itemCount

        offertemp.img1 = if (count >= 1) imgBasePath + "img1.jpg" else "empty"
        offertemp.img2 = if (count >= 2) imgBasePath + "img2.jpg" else "empty"
        offertemp.img3 = if (count >= 3) imgBasePath + "img3.jpg" else "empty"

        return offertemp
    }

    override fun onFragClose(list: ArrayList<Bitmap>) {
        binding.scrolview.visibility = View.VISIBLE
        imageViewAdapter.update(list)
        chooseImageFrag = null
    }

    fun openChoosenImageFrag(newlist: ArrayList<Uri>?) {
        chooseImageFrag = ImageListFragment(this)
        if (newlist != null) chooseImageFrag?.resizeSelectedImages(newlist, true, this)
        binding.scrolview.visibility = View.GONE
        supportFragmentManager.beginTransaction()
            .replace(R.id.placeholder, chooseImageFrag!!)
            .commit()
    }

    private fun updateImageCounter() {
        binding.vpImages.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                val counter = "${position + 1}" + "/" + "${binding.vpImages.adapter?.itemCount}"
                binding.imagecounter.text = counter
            }
        })
    }

    override fun onStop() {
        super.onStop()
        firebaseViewModel.liveOffersData.removeObservers(this)
        Picasso.get().cancelTag(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        firebaseViewModel.liveOffersData.removeObservers(this)
        Picasso.get().cancelTag(this)
    }
}