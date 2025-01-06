package com.example.barterly
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.example.barterly.databinding.ActivityMainBinding
import com.example.barterly.dialoghelper.DialogConst
import com.example.barterly.dialoghelper.DialogHelper
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity(),OnNavigationItemSelectedListener {
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
    private fun init(){
        var toggle = ActionBarDrawerToggle(this,binding.drawerid,binding.mainContent.toolbar,R.string.open,R.string.close   )
        binding.drawerid.addDrawerListener(toggle)
        toggle.syncState()
        binding.navview.setNavigationItemSelectedListener(this)
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
            }
        binding.drawerid.closeDrawer(GravityCompat.START)
    return true
    }

}

interface OnCardClickListener {
    fun onCardClick(position: Int)
}