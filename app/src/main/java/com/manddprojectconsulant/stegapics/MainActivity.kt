package com.manddprojectconsulant.stegapics

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.manddprojectconsulant.stegapics.paymentintegration.InAppActivity
import com.manddprojectconsulant.stegapics.paymentintegration.RazorActivity
import com.manddprojectconsulant.stegapics.paymentintegration.StripeActivity
import com.manddprojectconsulant.stegapics.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.btnRazorPay.setOnClickListener {
            startActivity(Intent(this, RazorActivity::class.java))
        }
        binding.btnStrip.setOnClickListener {
            startActivity(Intent(this, StripeActivity::class.java))
        }
        binding.btnInApp.setOnClickListener {
            startActivity(Intent(this, InAppActivity::class.java))
        }
    }
}