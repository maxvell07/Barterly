package com.example.barterly
import android.app.ComponentCaller
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.example.barterly.accounthelper.GoogleAccConst
import com.example.barterly.databinding.ActivityMainBinding
import com.example.barterly.dialoghelper.DialogConst
import com.example.barterly.dialoghelper.DialogHelper
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MainActivity : AppCompatActivity(),OnNavigationItemSelectedListener {
    private lateinit var tvAccount:TextView
    private lateinit var binding: ActivityMainBinding
    private val dialoghelper = DialogHelper(this)
    val myAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
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
        var toggle = ActionBarDrawerToggle(this,binding.drawerid,binding.mainContent.toolbar,R.string.open,R.string.close   )
        binding.drawerid.addDrawerListener(toggle)
        toggle.syncState()
        binding.navview.setNavigationItemSelectedListener(this)
        tvAccount = binding.navview.getHeaderView(0).findViewById(R.id.tvaccountemail)
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
}

