package com.hedaia.gobarber

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.hedaia.gobarber.databinding.ActivityCustomerBinding

class CustomerActivity : AppCompatActivity() {
    lateinit var binding: ActivityCustomerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityCustomerBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}