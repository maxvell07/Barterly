package com.example.barterly.presentation.view.act

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.barterly.di.BarterlyApp
import com.example.barterly.R
import com.example.barterly.data.model.FiltersCriteries
import com.example.barterly.data.source.accounthelper.listener
import com.example.barterly.presentation.adapters.offerlistener
import com.example.barterly.presentation.adapters.OffersRcAdapter
import com.example.barterly.databinding.ActivityMainBinding
import com.example.barterly.presentation.dialoghelper.DialogConst
import com.example.barterly.presentation.dialoghelper.DialogHelper
import com.example.barterly.presentation.view.fragment.FilterDialogFragment
import com.example.barterly.data.model.Offer
import com.example.barterly.presentation.viewmodel.FirebaseViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class MainActivity : AppCompatActivity(), OnNavigationItemSelectedListener, offerlistener {
    private lateinit var tvAccount: TextView
    private lateinit var imAccount: ImageView
    private lateinit var binding: ActivityMainBinding
    private val dialoghelper = DialogHelper(this)
    val myAuth = Firebase.auth
    private lateinit var firebaseViewModel: FirebaseViewModel
    val offeradapter = OffersRcAdapter(this)
    lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseViewModel = (application as BarterlyApp).firebaseViewModel
        init()
        initRcView()
        initViewModel()
        firebaseViewModel.loadoffers()
        bottomNavMenuOnClick()
        scrollListner()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.filter -> {
                val filterDialog = FilterDialogFragment { category, city, country, priceFrom, priceTo, sortByTime ->
                    val criteria = FiltersCriteries(
                        category = category,
                        city = city,
                        country = country,
                        priceFrom = priceFrom,
                        priceTo = priceTo,
                        sortByTime = sortByTime
                    )
                    firebaseViewModel.applyFilters(criteria)
                }

                if (!isFinishing && !isDestroyed) {
                    filterDialog.show(supportFragmentManager, "FilterDialog")
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    private fun onActivityResult() {
        googleSignInLauncher = registerForActivityResult(
            ActivityResultContracts
                .StartActivityForResult()
        ) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    dialoghelper.accHelper.signInFireBaseWithGoogle(account.idToken!!)
                }
            } catch (e: ApiException) {
                Log.d("MyLog", "Api error: ${e.message}")
            }
        }
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
        firebaseViewModel.filteredOffers.observe(this) { list ->
            offeradapter.updateAdapter(list)
            binding.mainContent.tvEmpty.visibility =
                if (list.isEmpty()) View.VISIBLE else View.GONE
            binding.progress.visibility = View.GONE
            binding.mainContent.rcView.scrollToPosition(0)
        }

    }

    private fun init() {
        setSupportActionBar(binding.mainContent.toolbar)
        onActivityResult()
        var toggle = ActionBarDrawerToggle(
            this, binding.drawerid, binding.mainContent.toolbar,
            R.string.open,
            R.string.close
        )

        binding.drawerid.addDrawerListener(toggle)
        toggle.syncState()
        binding.navview.setNavigationItemSelectedListener(this)
        tvAccount = binding.navview.getHeaderView(0).findViewById(R.id.tvaccountemail)
        imAccount = binding.navview.getHeaderView(0).findViewById(R.id.imageView)
    }

    private fun bottomNavMenuOnClick() = with(binding) {
        mainContent.bnavview.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.new_offer -> {
                    if (myAuth.currentUser?.isAnonymous !=true){
                    val i = Intent(this@MainActivity, EditOfferAct::class.java)
                    startActivity(i)}
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
                    imAccount.setImageResource(R.drawable.profile_image)
                }
            })
        } else if (user.isAnonymous) {
            tvAccount.text = "Guest"
            imAccount.setImageResource(R.drawable.profile_image)

        } else if (!user.isAnonymous) {
            tvAccount.text = user.email
            Picasso.get().load(user.photoUrl).into(imAccount)
        }
    }


    override fun onFavClick(offer: Offer) {
        firebaseViewModel.onFavClick(offer)
    }

    override fun onOfferViewed(offer: Offer) {
        firebaseViewModel.offerViewed(offer)

    }


    override fun ondeleteoffer(offer: Offer) {
        firebaseViewModel.deleteoffer(offer)
    }

    private fun scrollListner() = with(binding.mainContent) {
        rcView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(
                recyclerView: RecyclerView,
                newState: Int
            ) {
                super.onScrollStateChanged(recyclerView, newState)
//                if (!recyclerView.canScrollVertically(SCROL) && newState == RecyclerView.SCROLL_STATE_IDLE) {
//                  пока не использую
//                }
            }
        })
    }

    companion object {

        const val EDIT_STATE = "edit_state"
//      const val OFFER_DATA = "offer_data"
        const val OFFER_KEY = "offer_key"
        const val SCROL = 1
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        firebaseViewModel.liveOffersData.removeObservers(this)
    }
}

