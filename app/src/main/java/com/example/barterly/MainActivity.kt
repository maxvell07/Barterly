package com.example.barterly
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.Barterly.myapp.CardAdapter
import com.example.Barterly.myapp.CardItem
import com.example.Barterly.myapp.FirstFragment
import com.example.Barterly.myapp.SecondFragment
import com.example.Barterly.myapp.ThirdFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity(), OnCardClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val firstFragment= FirstFragment()
        val secondFragment= SecondFragment()
        val thirdFragment= ThirdFragment()
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)

        setCurrentFragment(firstFragment)

        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    // Показываем RecyclerView, скрываем FrameLayout
                    setCurrentFragment(firstFragment)
                    recyclerView.visibility = View.VISIBLE
                    supportFragmentManager.beginTransaction().hide(firstFragment).commit()
                }
                R.id.chat -> {
                    // Показываем фрагмент, скрываем RecyclerView
                    setCurrentFragment(secondFragment)
                    recyclerView.visibility = View.GONE
                }
                R.id.settings -> {
                    // Показываем фрагмент, скрываем RecyclerView
                    setCurrentFragment(thirdFragment)
                    recyclerView.visibility = View.GONE
                }
            }
            true
        }

        // По умолчанию показываем RecyclerView
        recyclerView.visibility = View.VISIBLE

        val items = listOf(
            CardItem(R.drawable.image, "Card 0"),
            CardItem(R.drawable.image, "Card 1"),
            CardItem(R.drawable.image, "Card 2"),
            CardItem(R.drawable.image, "Card 3"),
            CardItem(R.drawable.image, "Card 4"),
            CardItem(R.drawable.image, "Card 5")
        )
        items[0]

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = CardAdapter(items, this)

    }
    private fun setCurrentFragment(fragment: Fragment)=
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment,fragment)
            commit()
        }
    override fun onCardClick(position: Int) {
        // Обрабатываем клик по карточке
        Toast.makeText(this, "Card $position clicked", Toast.LENGTH_SHORT).show()
    }

}
interface OnCardClickListener {
    fun onCardClick(position: Int)
}