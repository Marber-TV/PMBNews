package com.pablo.pmbnews.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.analytics.FirebaseAnalytics
import com.pablo.pmbnews.App
import com.pablo.pmbnews.R
import com.pablo.pmbnews.bbdd.NewsDatabase
import com.pablo.pmbnews.bbdd.NewsRepository
import com.pablo.pmbnews.bbdd.NewsViewModel
import com.pablo.pmbnews.bbdd.NewsViewModelFactory
import com.pablo.pmbnews.databinding.ActivityInicioBinding
import com.pablo.pmbnews.ui.fragments.FragmentBreakingNews
import com.pablo.pmbnews.ui.fragments.FragmentSavedNews

class Inicio : AppCompatActivity() {
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var binding: ActivityInicioBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInicioBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        val email = (applicationContext as App).auth.getCurrentUser()
        val user = email?.email?.substringBefore("@")
        Toast.makeText(this, "Bienvenido $user", Toast.LENGTH_SHORT).show()
        setupViewModel()
        replaceFragment(FragmentBreakingNews())
        setupBottomNavigation()
        replaceFragment(FragmentBreakingNews())
    }

    private fun setupViewModel() {
        val application = this.getApplication()
        val dao = NewsDatabase.getDatabase(application).newsDao()
        val repository = NewsRepository(dao)
        val factory = NewsViewModelFactory(application, repository)
        val viewModel = ViewModelProvider(this, factory).get(NewsViewModel::class.java)
        viewModel.fetchNews()
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    replaceFragment(FragmentBreakingNews())
                    return@OnNavigationItemSelectedListener true
                }

                R.id.navigation_saved -> {
                    replaceFragment(FragmentSavedNews())
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_search -> {
                    val intent = Intent(this@Inicio, Search::class.java)
                    startActivity(intent)
                }

                R.id.navigation_logout -> {
                    (applicationContext as App).auth.signOut()
                    finish()
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        })
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_view, fragment)
            .commit()
    }

    override fun onResume() {
        super.onResume()
        binding.bottomNavigation.menu.findItem(R.id.navigation_home).isChecked = true
    }


    fun hideBottomNav() {
        findViewById<BottomNavigationView>(R.id.bottom_navigation).visibility = View.GONE
    }

    fun showBottomNav() {
        findViewById<BottomNavigationView>(R.id.bottom_navigation).visibility = View.VISIBLE
    }


}
