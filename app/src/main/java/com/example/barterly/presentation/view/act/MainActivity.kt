package com.example.barterly.presentation.view.act

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.example.barterly.di.BarterlyApp
import com.example.barterly.R
import com.example.barterly.data.source.accounthelper.AccountHelper.Listener
import com.example.barterly.databinding.ActivityMainBinding
import com.example.barterly.presentation.dialoghelper.DialogConst
import com.example.barterly.presentation.dialoghelper.DialogHelper
import com.example.barterly.presentation.view.fragment.FavoritesFragment
import com.example.barterly.presentation.view.fragment.FilterFragment
import com.example.barterly.presentation.view.fragment.HomeFragment
import com.example.barterly.presentation.view.fragment.MyOffersFragment
import com.example.barterly.presentation.view.fragment.NoInternetFragment
import com.example.barterly.presentation.viewmodel.FirebaseViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    NoInternetFragment.RetryListener {

    private lateinit var tvAccount: TextView
    private lateinit var imAccount: ImageView
    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseViewModel: FirebaseViewModel
    private lateinit var dialoghelper: DialogHelper
    lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>

    private var filterMenuItem: MenuItem? = null
    private var currentTabId = R.id.home
    val myAuth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkNetworkAndStart()
        firebaseViewModel = (application as BarterlyApp).firebaseViewModel
        dialoghelper = DialogHelper(this)

        initUI()
        initGoogleAuth()
        setupBottomNav()
    }

    private fun checkNetworkAndStart() {
        if (isNetworkAvailable(this) && !isFinishing && !isDestroyed) {
            setFragment(HomeFragment())
            findViewById<FrameLayout>(R.id.fullscreen_container).visibility = View.GONE
        } else {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fullscreen_container, NoInternetFragment())
                .commit()
            findViewById<FrameLayout>(R.id.fullscreen_container).visibility = View.VISIBLE
        }
    }

    fun isNetworkAvailable(context: Context): Boolean {
        val cm = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo?.isConnectedOrConnecting == true
    }

    fun showNoInternetFragment() {
        findViewById<FrameLayout>(R.id.fullscreen_container).visibility = View.VISIBLE
        supportFragmentManager.beginTransaction()
            .replace(R.id.fullscreen_container, NoInternetFragment())
            .commit()
    }

    private fun initUI() {
        setSupportActionBar(binding.mainContent.toolbar)

        val toggle = ActionBarDrawerToggle(
            this, binding.drawerid, binding.mainContent.toolbar,
            R.string.open, R.string.close
        )
        binding.drawerid.addDrawerListener(toggle)
        toggle.syncState()

        binding.navview.setNavigationItemSelectedListener(this)
        tvAccount = binding.navview.getHeaderView(0).findViewById(R.id.tvaccountemail)
        imAccount = binding.navview.getHeaderView(0).findViewById(R.id.imageView)
    }

    private fun initGoogleAuth() {
        googleSignInLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                account?.idToken?.let { dialoghelper.accHelper.signInFirebaseWithGoogle(it) }
            } catch (e: ApiException) {
                Log.d("MyLog", "Google sign-in error: ${e.message}")
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        filterMenuItem = menu?.findItem(R.id.filter)
        updateFilterMenuVisibility()
        return super.onCreateOptionsMenu(menu)
    }

    private fun updateFilterMenuVisibility() {
        filterMenuItem?.isVisible = (currentTabId == R.id.home)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.filter -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.main_content, FilterFragment.newInstance())
                    .addToBackStack(null)
                    .commit()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupBottomNav() = with(binding.mainContent) {
        bnavview.setOnItemSelectedListener { item ->
            currentTabId = item.itemId
            when (item.itemId) {
                R.id.new_offer -> {
                    if (myAuth.currentUser?.isAnonymous != true) {
                        startActivity(Intent(this@MainActivity, EditOfferAct::class.java))
                    }
                }

                R.id.home -> {
                    checkNetworkAndStart()
                    if (!isFinishing && !isDestroyed) {
                        setFragment(HomeFragment())
                    }

                    toolbar.title = getString(R.string.other)
                }

                R.id.my_offers -> {
                    checkNetworkAndStart()
                    if (!isFinishing && !isDestroyed) {
                        setFragment(MyOffersFragment())
                    }
                    toolbar.title = getString(R.string.ad1)
                }

                R.id.favorites -> {
                    checkNetworkAndStart()
                    if (!isFinishing && !isDestroyed) {
                        setFragment(FavoritesFragment())
                    }
                    toolbar.title = getString(R.string.offer_my_favs)
                }
            }
            updateFilterMenuVisibility()
            true
        }
    }

    private fun setFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    override fun onStart() {
        super.onStart()
        uiUpdate(myAuth.currentUser)
    }

    //    override fun onResume() {
//        super.onResume()
//        binding.mainContent.bnavview.selectedItemId = R.id.home
//        setFragment(HomeFragment())
//    }
    override fun onResume() {
        super.onResume()

        // Проверка, показываем ли мы уже NoInternetFragment
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (currentFragment is NoInternetFragment) {
            return
        }
        if (!isFinishing && !isDestroyed) {
            binding.mainContent.bnavview.selectedItemId = R.id.home
            setFragment(HomeFragment())
        }
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.my_ads -> {
            }

            R.id.id_sign_up -> {
                dialoghelper.createSignDialog(DialogConst.SIGN_UP_STATE)
            }

            R.id.id_sign_in -> {
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
            dialoghelper.accHelper.signInAnonymously(object : Listener {
                override fun onComplete() {
                    tvAccount.text = "Guest"
                    imAccount.setImageResource(R.drawable.profile_image)
                }
            })
        } else {
            tvAccount.text = user.email ?: "Guest"
            if (user.photoUrl != null) {
                Picasso.get().load(user.photoUrl).into(imAccount)
            } else {
                imAccount.setImageResource(R.drawable.profile_image)
            }
        }
    }

    override fun onRetrySuccess() {
        if (!isFinishing && !isDestroyed) {
            findViewById<FrameLayout>(R.id.fullscreen_container).visibility = View.GONE
            setFragment(HomeFragment())
        }
    }

    companion object {
        const val EDIT_STATE = "edit_state"
        const val OFFER_KEY = "offer_key"
    }
}
