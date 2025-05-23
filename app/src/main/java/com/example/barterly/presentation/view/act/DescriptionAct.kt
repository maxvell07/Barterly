package com.example.barterly.presentation.view.act

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.barterly.di.BarterlyApp
import com.example.barterly.R
import com.example.barterly.presentation.adapters.DialogOfferAdapter
import com.example.barterly.presentation.adapters.ImageAdapter
import com.example.barterly.databinding.ActivityDescriptionBinding
import com.example.barterly.presentation.dialogs.BuyOrTradeDialog
import com.example.barterly.data.model.Offer
import com.example.barterly.presentation.viewmodel.FirebaseViewModel
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target

class DescriptionAct : AppCompatActivity() {
    lateinit var binding: ActivityDescriptionBinding
    lateinit var adapter:ImageAdapter
    private lateinit var firebaseViewModel: FirebaseViewModel
    var offer: Offer? =null
    var selectedOfferForTrade:Offer? = null
    var dialog: BuyOrTradeDialog? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDescriptionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.desc)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        firebaseViewModel = (application as BarterlyApp).firebaseViewModel
        init()
        binding.fbTel.setOnClickListener{call()}
        binding.fbEmail.setOnClickListener{
            dialog = BuyOrTradeDialog(

                onBuyClicked = {
                    val phone = offer?.phone
                    if (!phone.isNullOrBlank()) {
                        openWhatsAppDirect(phone)
                    } else {
                        Toast.makeText(this, "Номер телефона недоступен", Toast.LENGTH_SHORT).show()
                    }

                },
                onTradeClicked = {
                    firebaseViewModel.myOffersData.value.let { offers ->
                        // Проверяем, активна ли еще Activity

                        val dialogView = layoutInflater.inflate(R.layout.dialog_with_offers_list, null)
                        val recyclerView = dialogView.findViewById<RecyclerView>(R.id.recyclerViewOptions)
                        recyclerView.layoutManager = LinearLayoutManager(this)

                        val alertDialog = AlertDialog.Builder(this@DescriptionAct)
                            .setView(dialogView)
                            .create()

                        recyclerView.adapter = DialogOfferAdapter(offers as List<Offer>) { selectedOffer ->
                            selectedOfferForTrade = selectedOffer
                            Toast.makeText(this, "Выбран: ${selectedOffer.title}", Toast.LENGTH_SHORT).show()
                            sendOfferToWhatsApp(selectedOfferForTrade)
                            alertDialog.dismiss()
                        }

                        dialogView.findViewById<Button>(R.id.btnClose).setOnClickListener {
                            alertDialog.dismiss()
                        }

                        // Защита от WindowLeaked
                        if (!isFinishing && !isDestroyed) {
                            alertDialog.show()
                        }
                    }
                }



            )
            dialog?.show(supportFragmentManager, "BuyOrTradeDialog")
        }

    }
    private fun sendOfferToWhatsApp(myoffer: Offer?) {
        var phoneNumber = offer?.phone?.replace(" ", "") ?: return // Убираем пробелы

        // Если номер начинается с 8, заменяем его на +7 (международный формат для России)
        if (phoneNumber.startsWith("8")) {
            phoneNumber = "7" + phoneNumber.substring(1)
        }

        // Проверяем, чтобы номер был в международном формате
        if (phoneNumber.length != 11 || !phoneNumber.startsWith("7")) {
            Toast.makeText(this, "Неверный формат номера", Toast.LENGTH_SHORT).show()
            return
        }

        val message = """
        Привет! Предлагаю обмен:
        
        ${myoffer?.title ?: "Без названия"}
        Цена: ${myoffer?.price ?: "0"} ₽
        ${myoffer?.description ?: ""}
    """.trimIndent()

        // Ссылки на картинки (предполагается, что картинки уже загружены в облако)
        val imageUrl1 = myoffer?.img1 ?: ""
        val imageUrl2 = myoffer?.img2 ?: ""

        // Добавляем ссылки на картинки в сообщение
        val messageWithImages = """
        $message
        
        Картинка 1: $imageUrl1
        Картинка 2: $imageUrl2
    """.trimIndent()

        try {
            // Формируем правильный URL для WhatsApp с текстом и картинками
            val uri = "https://wa.me/$phoneNumber?text=${Uri.encode(messageWithImages)}".toUri()

            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                setPackage("com.whatsapp") // Указываем пакет WhatsApp
            }

            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Не удалось открыть WhatsApp", Toast.LENGTH_SHORT).show()
            Log.d("whatsapp2","${e.message}")
        }
    }



    fun openWhatsAppDirect(phoneNumber: String) {
        try {
            val uri = "smsto:$phoneNumber".toUri() // SMS URI
            val intent = Intent(Intent.ACTION_SENDTO, uri)
            intent.setPackage("com.whatsapp") // Указываем пакет WhatsApp
            this.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "WhatsApp не установлен", Toast.LENGTH_SHORT).show()
            Log.d("whatsapp","${e.message}")
        }
    }

    private fun init(){
        adapter = ImageAdapter()
        binding.apply {
            viewPager.adapter = adapter
        }
        getIntentFromMainAct()
        updateImageCounter()
        firebaseViewModel.loadMyOffers()
    }

    private fun getIntentFromMainAct(){
        var key = intent.getSerializableExtra(MainActivity.OFFER_KEY) as String
        firebaseViewModel.liveOffersData.observe(this) { offers ->
            offer = offers?.find { it.key == key }
            offer?.let { fillOfferViews(it) }
        }
    }
    private fun fillOfferViews(offer: Offer) = with(binding) {
        //observelive data
        // Обновляем поля UI
        tvCountry.text = offer.country
        tvTel.text = offer.phone
        tvCity.text = offer.city
        tvTitleDes.text = offer.title
        tvAdress.text = offer.adress
        tvDesc.text = offer.description
        tvPrice.text = offer.price
        tvCategory.text = offer.category

        adapter.array.clear()

        val images = ArrayList<String>().apply {
            offer.img1?.let { add(it) }
            offer.img2?.let { add(it) }
            offer.img3?.let { add(it) }
        }

        loadImagesToBitmaps(images) { bitmaps ->
            adapter.array.clear()
            adapter.array.addAll(bitmaps)
            adapter.notifyDataSetChanged() //плохо, но пока так
        }

    }

    private fun loadImagesToBitmaps(urls: List<String>, callback: (List<Bitmap>) -> Unit) {
        val bitmaps = mutableListOf<Bitmap>()
        var loadedCount = 0

        for (url in urls) {
            if (!isDestroyed) {
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
        }}
    }

    private fun call() {
        val rawPhone = offer?.phone ?: return

        // Очищаем номер
        var phone = rawPhone.replace("[^\\d+]".toRegex(), "")

        // Преобразуем 8 → +7
        if (phone.startsWith("8") && phone.length == 11) {
            phone = "+7" + phone.substring(1)
        }

        if (!phone.startsWith("+")) {
            phone = "+$phone"
        }

        Log.d("CALL_DEBUG", "Готовый номер: $phone")

        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
        try {
            startActivity(intent)
        } catch (e: Exception) {
            Log.e("CALL_ERROR", "${e.message}", e)
        }
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
        dialog?.dismiss()
        Picasso.get().cancelTag(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        firebaseViewModel.liveOffersData.removeObservers(this)
        dialog?.dismiss()
        Picasso.get().cancelTag(this)
    }
}