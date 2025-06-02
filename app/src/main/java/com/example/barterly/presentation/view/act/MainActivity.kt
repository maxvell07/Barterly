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
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.example.barterly.di.BarterlyApp
import com.example.barterly.R
import com.example.barterly.data.source.accounthelper.AccountHelper.Listener
import com.example.barterly.databinding.ActivityMainBinding
import com.example.barterly.presentation.dialoghelper.DialogConst
import com.example.barterly.presentation.dialoghelper.DialogHelper
import com.example.barterly.presentation.view.fragment.NoInternetFragment
import com.example.barterly.presentation.viewmodel.FirebaseViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.android.material.bottomnavigation.BottomNavigationView

import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    NoInternetFragment.RetryListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseViewModel: FirebaseViewModel
    private lateinit var dialogHelper: DialogHelper
    private lateinit var tvAccount: TextView
    private lateinit var imAccount: ImageView
    lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>
    private lateinit var editOfferLauncher: ActivityResultLauncher<Intent>
    private lateinit var navController: NavController
    private var filterMenuItem: MenuItem? = null
    private var currentTabId = R.id.home
    val myAuth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        firebaseViewModel = (application as BarterlyApp).firebaseViewModel
        dialogHelper = DialogHelper(this)

        initUI()
        initGoogleAuth()
        setupBottomNav()
        checkNetworkAndStart()

        editOfferLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                firebaseViewModel.loadoffers()
            }
            currentTabId = R.id.home
        }
    }

    private fun checkNetworkAndStart() {
        val fullscreen = findViewById<FrameLayout>(R.id.fullscreen_container)
        if (isNetworkAvailable(this) && !isFinishing && !isDestroyed) {
            fullscreen.visibility = View.GONE
        } else {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fullscreen_container, NoInternetFragment())
                .commit()
            fullscreen.visibility = View.VISIBLE
        }
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        val cm = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo?.isConnectedOrConnecting == true
    }

    fun showNoInternetFragment() {
        val fullscreen = findViewById<FrameLayout>(R.id.fullscreen_container)
        fullscreen.visibility = View.VISIBLE
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
                account?.idToken?.let { dialogHelper.accHelper.signInFirebaseWithGoogle(it) }
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
                navController.navigate(R.id.filterFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupBottomNav() {
        // подключаем BottomNavigationView к NavController
        binding.mainContent.bnavview.setupWithNavController(navController)

        // Отдельно обрабатываем кнопку "новое объявление", чтобы не переключать вкладку
        binding.mainContent.bnavview.setOnItemSelectedListener { item ->
            if (item.itemId == R.id.new_offer) {
                if (myAuth.currentUser?.isAnonymous != true) {
                    val intent = Intent(this, EditOfferAct::class.java)
                    editOfferLauncher.launch(intent)
                }
                false // чтобы навигация не переключилась на новую вкладку
            } else {
                // Передать обработку NavController
                NavigationUI.onNavDestinationSelected(item, navController)
                true
            }.also {
                updateFilterMenuVisibility()
            }
        }

        // Подписываемся на изменения выбранного пункта меню, чтобы обновлять заголовок
        navController.addOnDestinationChangedListener { _, destination, _ ->
            currentTabId = destination.id
            when (destination.id) {
                R.id.home -> binding.mainContent.toolbar.title = getString(R.string.other)
                R.id.my_offers -> binding.mainContent.toolbar.title = getString(R.string.ad1)
                R.id.favorites -> binding.mainContent.toolbar.title = getString(R.string.offer_my_favs)
                else -> binding.mainContent.toolbar.title = getString(R.string.app_name)
            }
            updateFilterMenuVisibility()
        }
    }


    override fun onStart() {
        super.onStart()
        uiUpdate(myAuth.currentUser)
    }

    override fun onResume() {
        super.onResume()
        if (!isFinishing && !isDestroyed &&
            supportFragmentManager.findFragmentById(R.id.fullscreen_container) !is NoInternetFragment
        ) {
            binding.mainContent.bnavview.selectedItemId = currentTabId
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.my_ads -> { /* TODO */ }

            R.id.id_sign_up -> {
                dialogHelper.createSignDialog(DialogConst.SIGN_UP_STATE)
            }

            R.id.id_sign_in -> {
                dialogHelper.createSignDialog(DialogConst.SIGN_IN_STATE)
            }

            R.id.id_sign_out -> {
                if (myAuth.currentUser?.isAnonymous == true) {
                    binding.drawerid.closeDrawer(GravityCompat.START)
                    return true
                }
                uiUpdate(null)
                myAuth.signOut()
                dialogHelper.accHelper.signOutGoogle()
            }
        }
        binding.drawerid.closeDrawer(GravityCompat.START)
        return true
    }

    fun uiUpdate(user: FirebaseUser?) {
        if (user == null) {
            dialogHelper.accHelper.signInAnonymously(object : Listener {
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
            navController.navigate(R.id.home)
        }
    }

    companion object {
        const val EDIT_STATE = "edit_state"
        const val OFFER_KEY = "offer_key"
    }
}
