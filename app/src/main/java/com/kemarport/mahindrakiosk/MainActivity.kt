package com.kemarport.mahindrakiosk

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.kemarport.mahindrakiosk.databinding.ActivityMainBinding
import com.kemarport.mahindrakiosk.home.KioskHomeActivity

class MainActivity : AppCompatActivity() {
    lateinit var binding:ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_main)
        binding.switchBtn.setOnClickListener {
            startActivity(Intent(this,KioskHomeActivity::class.java))

        }

    }
}