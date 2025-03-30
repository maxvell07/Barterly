package com.example.barterly.act

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.barterly.BarterlyApp
import com.example.barterly.R
import com.example.barterly.adapters.ImageAdapter
import com.example.barterly.databinding.ActivityDescriptionBinding
import com.example.barterly.model.Offer
import com.example.barterly.viewmodel.FirebaseViewModel
import com.squareup.picasso.Picasso

class DescriptionAct : AppCompatActivity() {
    lateinit var binding: ActivityDescriptionBinding
    lateinit var adapter:ImageAdapter
    private lateinit var firebaseViewModel: FirebaseViewModel
    var offer: Offer? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityDescriptionBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.desc)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        firebaseViewModel = (application as BarterlyApp).firebaseViewModel
        init()
        binding.fbTel.setOnClickListener{call()}
        binding.fbEmail.setOnClickListener{}
    }
    private fun init(){
        adapter = ImageAdapter()
        binding.apply {
            viewPager.adapter = adapter
        }
        getIntentFromMainAct()
        updateImageCounter()
    }

    private fun getIntentFromMainAct(){
        var key = intent.getSerializableExtra(MainActivity.OFFER_KEY) as String
        firebaseViewModel.liveOffersData.observe(this) { offers ->
            offer = offers.find { it.key == key }
            offer?.let { fillOfferViews(it) }
        }
    }
    private fun fillOfferViews(offer: Offer) = with(binding) {
        //observelive data
        // Обновляем поля UI
        tvCountry.text = offer.country
        tvTel.setText(offer.phone)
        tvCity.text = offer.city
        tvTitleDes.setText(offer.title)
        tvAdress.setText(offer.adress)
        tvDesc.setText(offer.description)
        tvPrice.setText(offer.price)
        tvCategory.setText(offer.category)

        adapter.array.clear()

        val images = ArrayList<String>().apply {
            offer.img1?.let { add(it) }
            offer.img2?.let { add(it) }
            offer.img3?.let { add(it) }
        }

        loadImagesToBitmaps(images) { bitmaps ->
            adapter.array.clear()
            adapter.array.addAll(bitmaps)
            adapter.notifyDataSetChanged()
        }

    }

    private fun loadImagesToBitmaps(urls: List<String>, callback: (List<Bitmap>) -> Unit) {
        val bitmaps = mutableListOf<Bitmap>()
        var loadedCount = 0

        for (url in urls) {
            if (!isDestroyed) {
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
        }}
    }

    private fun call(){
        val Calluri = "tel:${offer?.phone}"
        val iCall = Intent(Intent.ACTION_DIAL)
        iCall.data = Calluri.toUri()
        startActivity(iCall)
    }
    private fun updateImageCounter(){
        binding.viewPager.registerOnPageChangeCallback(object :ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                val counter ="${position+1}"+"/"+"${binding.viewPager.adapter?.itemCount}"
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