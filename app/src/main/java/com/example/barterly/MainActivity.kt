package com.example.barterly
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.barterly.accounthelper.GoogleAccConst
import com.example.barterly.act.EditAdsAct
import com.example.barterly.adapters.OffersRcAdapter
import com.example.barterly.data.Offer
import com.example.barterly.database.DbManager
import com.example.barterly.database.ReadDataCallback
import com.example.barterly.databinding.ActivityMainBinding
import com.example.barterly.dialoghelper.DialogConst
import com.example.barterly.dialoghelper.DialogHelper
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity(),OnNavigationItemSelectedListener,ReadDataCallback {
    private lateinit var tvAccount:TextView
    private lateinit var binding: ActivityMainBinding
    private val dialoghelper = DialogHelper(this)
    val myAuth = Firebase.auth
    val dbManager = DbManager(this)
    val offeradapter = OffersRcAdapter(myAuth)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        initRcView()
        dbManager.readDataFromDb()

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean { //переход в новое активити
        if (item.itemId == R.id.id_new_ads){
            val i = Intent(this,EditAdsAct::class.java)
            startActivity(i)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode ==GoogleAccConst.GOOGLE_SIGN_IN_REQUEST_CODE){
            Log.d("MyLog","Sign in Result")
            val task =GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException ::class.java)
                if (account!=null){
                    dialoghelper.accHelper.signInFireBaseWithGoogle(account.idToken!!)
                }
            }catch (e:ApiException){
                Log.d("MyLog", "Api error: ${e.message}")
            }
        }
        super.onActivityResult(requestCode, resultCode, data)

    }

    override fun onStart() {
        super.onStart()
        uiUpdate(myAuth.currentUser)
    }
    private fun init(){
        setSupportActionBar(binding.mainContent.toolbar)
        var toggle = ActionBarDrawerToggle(this,binding.drawerid,binding.mainContent.toolbar,R.string.open,R.string.close   )
        binding.drawerid.addDrawerListener(toggle)
        toggle.syncState()
        binding.navview.setNavigationItemSelectedListener(this)
        tvAccount = binding.navview.getHeaderView(0).findViewById(R.id.tvaccountemail)
    }

    private fun initRcView(){
        binding.apply {

            mainContent.rcView.layoutManager = LinearLayoutManager(this@MainActivity)
            mainContent.rcView.adapter = offeradapter
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

            when(item.itemId){

                R.id.my_ads ->{
                    Toast.makeText(this,"Main1",Toast.LENGTH_SHORT).show()
                }
                R.id.id_sign_up ->{
                    //Toast.makeText(this,"Main2",Toast.LENGTH_SHORT).show()
                    dialoghelper.createSignDialog(DialogConst.SIGN_UP_STATE)
                }
                R.id.id_sign_in ->{
                    //Toast.makeText(this,"Main2",Toast.LENGTH_SHORT).show()
                    dialoghelper.createSignDialog(DialogConst.SIGN_IN_STATE)
                }
                R.id.id_sign_out ->{
                    uiUpdate(null)
                    myAuth.signOut()
                    dialoghelper.accHelper.signOutGoogle()
                }
            }

        binding.drawerid.closeDrawer(GravityCompat.START)
    return true
    }

    fun uiUpdate(user: FirebaseUser?){

        tvAccount.text = if (user ==null){
            resources.getString(R.string.not_reg_email_title)
        }else {
            user.email
        }
    }

    override fun readData(list: List<Offer>) {
        offeradapter.updateAdapter(list)
    }
}

