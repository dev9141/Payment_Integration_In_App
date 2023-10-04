package com.manddprojectconsulant.stegapics.paymentintegration

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.manddprojectconsulant.stegapics.R
import com.manddprojectconsulant.stegapics.databinding.ActivityRazorBinding
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import org.json.JSONObject

class RazorActivity : AppCompatActivity(), PaymentResultListener {
    lateinit var binding:ActivityRazorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_razor)

        Checkout.preload(this@RazorActivity)

        binding.btnPay.setOnClickListener {
            startPayment()
        }
    }

    private fun startPayment() {
        val checkout = Checkout()
        checkout.setKeyID("rzp_test_MzCzHuIsMCZJrx")
        try {
            var options = JSONObject()
                options.put("name","Razorpay Corp")
                options.put("description","Demoing Charges")
                //You can omit the image option to fetch the image from dashboard
                options.put("image","https://i.pinimg.com/736x/0d/cf/b5/0dcfb548989afdf22afff75e2a46a508.jpg")
                options.put("currency","INR")
                options.put("amount",binding.etAmount.text.toString().toInt())
                options.put("send_sms_hash",true);

                val prefill = JSONObject()
                prefill.put("email","devnesss.vis@gmail.com")
                prefill.put("contact","8160831363")

                options.put("prefill",prefill)

            checkout.open(this@RazorActivity ,options)
        }catch (e: Exception){
            Toast.makeText(this@RazorActivity,"Error in payment: "+ e.message,Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    override fun onPaymentSuccess(p0: String?) {
        binding.tvPaymentStatus.visibility = View.VISIBLE
        binding.tvPaymentStatus.text = "$p0, Payment Successful"
        binding.tvPaymentStatus.setTextColor(Color.GREEN)
    }

    override fun onPaymentError(p0: Int, p1: String?) {
        binding.tvPaymentStatus.visibility = View.VISIBLE
        binding.tvPaymentStatus.text = "p0: $p0, $p1,\n Payment Successful"
        binding.tvPaymentStatus.setTextColor(Color.RED)
    }
}