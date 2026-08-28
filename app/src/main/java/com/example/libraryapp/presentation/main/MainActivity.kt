package com.example.libraryapp.presentation.main

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.libraryapp.R
import com.example.libraryapp.databinding.ActivityMainBinding
import com.example.libraryapp.utils.hide
import com.example.libraryapp.utils.show
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding?=null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainer) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNav.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.addMemberFragment -> binding.bottomNav.hide()
                R.id.addBookFragment -> binding.bottomNav.hide()
                R.id.issueReturnBookFragment -> binding.bottomNav.hide()
                else ->
                    binding.bottomNav.show()
            }
        }
    }

    fun selectBottomTab(itemId: Int) {
        binding.bottomNav.selectedItemId = itemId
    }
}