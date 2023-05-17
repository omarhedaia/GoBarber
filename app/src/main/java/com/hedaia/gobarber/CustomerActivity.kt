package com.hedaia.gobarber

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.hedaia.gobarber.databinding.ActivityCustomerBinding

class CustomerActivity : AppCompatActivity() {
    lateinit var binding: ActivityCustomerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityCustomerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navController = findNavController(R.id.fragmentContainerView2)
        NavigationUI.setupWithNavController(binding.bottomNavigation,navController)
        binding.bottomNavigation.setupWithNavController(navController)

    }
}