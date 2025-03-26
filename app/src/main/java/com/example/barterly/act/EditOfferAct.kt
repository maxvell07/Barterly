package com.example.barterly.act

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.barterly.BarterlyApp
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
import com.example.barterly.viewmodel.FirebaseViewModel
import com.squareup.picasso.Picasso
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
    private lateinit var firebaseViewModel: FirebaseViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditAdsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseViewModel = (application as BarterlyApp).firebaseViewModel
        init()
        checkeditstate()
        updateImageCounter()
    }

    private fun checkeditstate() {
        iseditstate = iseditstate()
        if (iseditstate) {
            var key = intent.getSerializableExtra(MainActivity.OFFER_KEY) as String
            firebaseViewModel.liveOffersData.observe(this) { offers ->
                offer = offers.find { it.key == key }
                offer?.let { fillViews(it) }
            }
            if (offer != null) {
                fillViews(offer!!)
            }
        }
    }

    private fun iseditstate(): Boolean {
        return intent.getBooleanExtra(
            MainActivity.EDIT_STATE,
            false
        ) //проверяем у интента открывшего true или false
    }

    private fun fillViews(offer: Offer) = with(binding) {
        //observelive data
        val images = listOfNotNull(
            offer.img1, offer.img2, offer.img3
        )
        loadImagesToBitmaps(images) { bitmaps ->
            imageViewAdapter.array.clear()
            imageViewAdapter.array.addAll(bitmaps)
            imageViewAdapter.notifyDataSetChanged()
        }

        // Обновляем поля UI

        selectCountry.text = offer.country
        selectCity.text = offer.city
        editTitleOffer.setText(offer.title)
        adresseditText.setText(offer.adress)
        phoneEditText.setText(offer.phone)
        selectCategory.setText(offer.category)
        editTextdiscription.setText(offer.description)
        priceeditrext.setText(offer.price)

    }

    private fun loadImagesToBitmaps(urls: List<String>, callback: (List<Bitmap>) -> Unit) {
        val bitmaps = mutableListOf<Bitmap>()
        var loadedCount = 0

        for (url in urls) {
            Picasso.get().load(url).tag(this).into(object : com.squareup.picasso.Target {
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
                    errorDrawable: android.graphics.drawable.Drawable?
                ) {
                    loadedCount++
                    if (loadedCount == urls.size) {
                        callback(bitmaps)
                    }
                }

                override fun onPrepareLoad(placeHolderDrawable: android.graphics.drawable.Drawable?) {}
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
//            firebaseViewModel.deleteAllImages(offer?.key.toString())
            ImagePiker.pickSeveralImages(this, 3)

        } else {
            openChoosenImageFrag(null)
            chooseImageFrag?.updateAdapterFromEdit(imageViewAdapter.array)

        }
    }

    fun onClickPublish(view: View) {
        firebaseViewModel.deleteAllImages(offer?.key.toString())
        val offertemp = filloffer()
        if (iseditstate) {
            Log.d("asdf","asdasff")
            dbmanager.publishOffer(offertemp.copy(key = offer?.key), onPublishFinish())
            uploadImagesAndDelete(offer?.key.toString())
        } else {
            dbmanager.publishOffer(offertemp, onPublishFinish())
            uploadImagesAndDelete(offertemp.key.toString())
        }
//        firebaseViewModel.loadoffers()
    }

    private fun uploadImagesAndDelete(offerKey: String) {
        val fileNames = listOf("img1.jpg", "img2.jpg", "img3.jpg")

        CoroutineScope(Dispatchers.IO).launch {
            imageViewAdapter.array.forEachIndexed { index, bitmap ->
                if (index < fileNames.size) {
                    val fileName = fileNames[index]
                    val file = saveBitmapToFile(bitmap, fileName)

                    if (file != null) {
                        firebaseViewModel.filerepository.uploadFile(offerKey, file).let { success ->
                            if (success.isSuccessful) {
                                file.delete() // Удаляем файл после успешной загрузки
                            }
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
                finish()
            }
        }
    }

    fun filloffer(): Offer {
        val offer: Offer
        binding.apply {
            offer = Offer(
                binding.selectCountry.text.toString(),
                binding.selectCity.text.toString(),
                binding.phoneEditText.text.toString(),
                binding.adresseditText.text.toString(),
                binding.selectCategory.text.toString(),
                binding.editTitleOffer.text.toString(),
                binding.priceeditrext.text.toString(),
                binding.editTextdiscription.text.toString(),
                "empty",
                "empty",
                "empty",
                dbmanager.db.push().key,// генерируем уникальный ключ офера
                "0",
                dbmanager.auth.uid,
                System.currentTimeMillis().toString()
            )
        }
        offer.img1 = "/images/" + offer.key.toString() + "/img1.jpg"
        offer.img2 = "/images/" + offer.key.toString() + "/img2.jpg"
        offer.img3 = "/images/" + offer.key.toString() + "/img3.jpg"
        return offer
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