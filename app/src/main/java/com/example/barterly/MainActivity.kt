package com.example.barterly
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.Barterly.myapp.CardAdapter
import com.example.Barterly.myapp.CardItem
import com.example.Barterly.myapp.FirstFragment
import com.example.Barterly.myapp.SecondFragment
import com.example.Barterly.myapp.ThirdFragment
import com.example.barterly.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener

class MainActivity : AppCompatActivity(),OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityMainBinding
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
                R.id.my_ads2 ->{
                    Toast.makeText(this,"Main2",Toast.LENGTH_SHORT).show()

                }
            }
        binding.drawerid.closeDrawer(GravityCompat.START)
    return true
    }

}

interface OnCardClickListener {
    fun onCardClick(position: Int)
}