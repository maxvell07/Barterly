package com.example.barterly.act

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.barterly.BarterlyApp
import com.example.barterly.R
import com.example.barterly.accounthelper.GoogleAccConst
import com.example.barterly.accounthelper.listener
import com.example.barterly.adapters.offerlistener
import com.example.barterly.adapters.OffersRcAdapter
import com.example.barterly.databinding.ActivityMainBinding
import com.example.barterly.dialoghelper.DialogConst
import com.example.barterly.dialoghelper.DialogHelper
import com.example.barterly.model.Offer
import com.example.barterly.model.OfferResult
import com.example.barterly.utils.Mapper.mapOfferToOfferResult
import com.example.barterly.viewmodel.FirebaseViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity(), OnNavigationItemSelectedListener, offerlistener {
    private lateinit var tvAccount: TextView
    private lateinit var binding: ActivityMainBinding
    private val dialoghelper = DialogHelper(this)
    val myAuth = Firebase.auth
    private lateinit var firebaseViewModel: FirebaseViewModel
    val offeradapter = OffersRcAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseViewModel = (application as BarterlyApp).firebaseViewModel
        init()
        initRcView()
        initViewModel()
        firebaseViewModel.loadoffers()
        bottomNavMenuOnClick()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == GoogleAccConst.GOOGLE_SIGN_IN_REQUEST_CODE) {
            Log.d("MyLog", "Sign in Result")
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    dialoghelper.accHelper.signInFireBaseWithGoogle(account.idToken!!)
                }
            } catch (e: ApiException) {
                Log.d("MyLog", "Api error: ${e.message}")
            }
        }
        super.onActivityResult(requestCode, resultCode, data)

    }

    override fun onStart() {
        super.onStart()
        uiUpdate(myAuth.currentUser)
    }

    override fun onResume() {
        super.onResume()
        binding.mainContent.bnavview.selectedItemId = R.id.home
    }

    private fun initViewModel() { //отслеживаем изменения в данных и обновляем адаптер
        firebaseViewModel.liveOffersData.observe(this) {
            offeradapter.updateAdapter(it)
            binding.mainContent.tvEmpty.visibility = if (it.isEmpty()) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }

    }


    private fun init() {
        setSupportActionBar(binding.mainContent.toolbar)
        var toggle = ActionBarDrawerToggle(
            this, binding.drawerid, binding.mainContent.toolbar,
            R.string.open,
            R.string.close
        )
        binding.drawerid.addDrawerListener(toggle)
        toggle.syncState()
        binding.navview.setNavigationItemSelectedListener(this)
        tvAccount = binding.navview.getHeaderView(0).findViewById(R.id.tvaccountemail)
    }

    private fun bottomNavMenuOnClick() = with(binding) {
        mainContent.bnavview.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.new_offer -> {
                    val i = Intent(this@MainActivity, EditAdsAct::class.java)
                    startActivity(i)
                }

                R.id.home -> {
                    firebaseViewModel.loadoffers()
                    mainContent.toolbar.title = getString(R.string.other)
                }

                R.id.my_offers -> {

                    firebaseViewModel.loadMyOffers()
                    mainContent.toolbar.title = getString(R.string.ad1)
                }

                R.id.favorites -> {
                    firebaseViewModel.loadMyFavs()
                    mainContent.toolbar.title = getString(R.string.offer_my_favs)
                }
            }
            true
        }
    }

    private fun initRcView() {
        binding.apply {

            mainContent.rcView.layoutManager = LinearLayoutManager(this@MainActivity)
            mainContent.rcView.adapter = offeradapter
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {

            R.id.my_ads -> {
                Toast.makeText(this, "Main1", Toast.LENGTH_SHORT).show()
            }

            R.id.id_sign_up -> {
                //Toast.makeText(this,"Main2",Toast.LENGTH_SHORT).show()
                dialoghelper.createSignDialog(DialogConst.SIGN_UP_STATE)
            }

            R.id.id_sign_in -> {
                //Toast.makeText(this,"Main2",Toast.LENGTH_SHORT).show()
                dialoghelper.createSignDialog(DialogConst.SIGN_IN_STATE)
            }

            R.id.id_sign_out -> {
                if (myAuth.currentUser?.isAnonymous == true) {
                    binding.drawerid.closeDrawer(GravityCompat.START)
                    return true
                }
                uiUpdate(null)
                myAuth.signOut()
                dialoghelper.accHelper.signOutGoogle()
            }
        }

        binding.drawerid.closeDrawer(GravityCompat.START)
        return true
    }

    fun uiUpdate(user: FirebaseUser?) {

        if (user == null) {
            dialoghelper.accHelper.signInAnonymously(object : listener {
                override fun onCompete() {
                    tvAccount.text = "Guest"
                }

            })
        } else {
            tvAccount.text = user.email
        }
    }

    companion object {

        const val EDIT_STATE = "edit_state"
        const val OFFER_DATA = "offer_data"
        const val OFFER_KEY = "offer_key"

    }

    override fun onFavClick(offer: OfferResult) {
        firebaseViewModel.onFavClick(offer)
    }

    override fun onOfferViewed(offer: OfferResult) {
        firebaseViewModel.offerViewed(offer)

    }


    override fun ondeleteoffer(offer: OfferResult) {
        firebaseViewModel.deleteoffer(offer)
    }
}

