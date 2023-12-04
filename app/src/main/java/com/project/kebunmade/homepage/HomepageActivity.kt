package com.project.kebunmade.homepage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.project.kebunmade.R
import com.project.kebunmade.databinding.ActivityHomepageBinding
import com.project.kebunmade.homepage.chat.ChatFragment
import com.project.kebunmade.homepage.order.OrderFragment
import com.project.kebunmade.homepage.product.ProductFragment

class HomepageActivity : AppCompatActivity() {

    private var binding: ActivityHomepageBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomepageBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val navView = findViewById<BottomNavigationView>(R.id.nav_view)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        navView.setOnNavigationItemSelectedListener { item: MenuItem ->
            var selectedFragment: Fragment? = ProductFragment()
            when (item.itemId) {
                R.id.navigation_home -> {
                    navView.menu.findItem(R.id.navigation_home).isEnabled = false
                    navView.menu.findItem(R.id.navigation_order).isEnabled = true
                    navView.menu.findItem(R.id.navigation_chat).isEnabled = true
                    selectedFragment = ProductFragment()
                }

                R.id.navigation_order -> {
                    navView.menu.findItem(R.id.navigation_home).isEnabled = true
                    navView.menu.findItem(R.id.navigation_order).isEnabled = false
                    navView.menu.findItem(R.id.navigation_chat).isEnabled = true
                    selectedFragment = OrderFragment()
                }

                R.id.navigation_chat -> {
                    navView.menu.findItem(R.id.navigation_home).isEnabled = true
                    navView.menu.findItem(R.id.navigation_order).isEnabled = true
                    navView.menu.findItem(R.id.navigation_chat).isEnabled = false
                    selectedFragment = ChatFragment()
                }

            }
            val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
            if (selectedFragment != null) {
                transaction.replace(R.id.container, selectedFragment)
            }
            transaction.commit()
            true
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}