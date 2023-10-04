package com.manddprojectconsulant.stegapics.paymentintegration

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.manddprojectconsulant.stegapics.R
import com.manddprojectconsulant.stegapics.databinding.ActivityStripeBinding
import com.manddprojectconsulant.stegapics.retrofit_setup.ApiClient
import com.manddprojectconsulant.stegapics.retrofit_setup.ApiInterface
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import com.stripe.android.paymentsheet.PaymentSheetResult.Completed
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StripeActivity : AppCompatActivity() {
    lateinit var binding: ActivityStripeBinding
    private lateinit var publishableKey: String
    private lateinit var secretKey: String
    private lateinit var customerId: String
    private lateinit var empiricalKey: String
    private lateinit var clientSecret: String
    private lateinit var paymentSheet: PaymentSheet
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_stripe)

        publishableKey =
            "pk_test_51Ns4wtSFnqfi4VMS1Yn2AlTBbXO0TEDEIP20BsprICfAWpH8hGn7y0Xwj2m7RuF4bn1EaJtrCLINEalssQKv4v7T00FTUfOrZd"
        secretKey =
            "sk_test_51Ns4wtSFnqfi4VMSgzz8XPZ4sTiDZYu5mgrRzPlQlMJg3Zcj3uVQKmZ6O3ik86M4skAFkDz2Dshi2QihRA0tG6oT00HH7YtiA1"

        PaymentConfiguration.init(this, publishableKey)
        getCustomerId()
        paymentSheet = PaymentSheet(this) {
            onPaymentResult(it)
        }

        binding.btnPay.setOnClickListener {
            getClientSecret()
        }
    }

    private fun paymentFlow() {
        paymentSheet.presentWithPaymentIntent(
            clientSecret,
            PaymentSheet.Configuration(
                "Devendra Payment",
                PaymentSheet.CustomerConfiguration(customerId, empiricalKey)
            )
        )
    }

    private fun onPaymentResult(paymentSheetResult: PaymentSheetResult) {
        if (paymentSheetResult is Completed) {
            Toast.makeText(this, "Payment Success", Toast.LENGTH_SHORT).show()
            binding.tvPaymentStatus.visibility = View.VISIBLE
            binding.tvPaymentStatus.text = "Payment Successful"
            binding.tvPaymentStatus.setTextColor(Color.GREEN)
        }
        if (paymentSheetResult is PaymentSheetResult.Failed) {
            Toast.makeText(this, "Payment Failed", Toast.LENGTH_SHORT).show()
            binding.tvPaymentStatus.visibility = View.VISIBLE
            binding.tvPaymentStatus.text = "Payment Successful"
            binding.tvPaymentStatus.setTextColor(Color.RED)
        }

    }

    private fun getCustomerId() {
        try {
            val apiInterface = ApiClient.getInstance().create(ApiInterface::class.java)

            val headerMap: HashMap<String, String> = hashMapOf()
            headerMap["Authorization"] = "Bearer $secretKey"
            val call = apiInterface.getCustomerId(headerMap)
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful) {
                        val stringResponse = response.body()!!.string()
                        val jsonObject = JSONObject(stringResponse)
                        customerId = jsonObject.optString("id")
                        Toast.makeText(
                            this@StripeActivity,
                            "customerId: $customerId",
                            Toast.LENGTH_SHORT
                        ).show()
                        getEmpiricalKey()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(
                        this@StripeActivity,
                        "Fail: ${t.printStackTrace()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        } catch (Ex: Exception) {
            Log.e("Error", Ex.localizedMessage)
        }
    }

    private fun getEmpiricalKey() {
        try {
            val apiInterface = ApiClient.getInstance().create(ApiInterface::class.java)

            val headerMap: HashMap<String, String> = hashMapOf()
            headerMap["Authorization"] = "Bearer $secretKey"
            headerMap["Stripe-Version"] = "2023-08-16"
            val hashMap: HashMap<String, String> = hashMapOf()
            hashMap["customer"] = customerId
            val call = apiInterface.getEphemeralKey(headerMap, hashMap)
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful) {
                        val stringResponse = response.body()!!.string()
                        val jsonObject = JSONObject(stringResponse)
                        empiricalKey = jsonObject.optString("id")
                        Toast.makeText(
                            this@StripeActivity,
                            "empiricalKey: $empiricalKey",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(
                        this@StripeActivity,
                        "Fail: ${t.printStackTrace()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        } catch (Ex: Exception) {
            Log.e("Error", Ex.localizedMessage)
        }
    }

    private fun getClientSecret() {
        try {
            val apiInterface = ApiClient.getInstance().create(ApiInterface::class.java)

            val headerMap: HashMap<String, String> = hashMapOf()
            headerMap["Authorization"] = "Bearer $secretKey"
            val hashMap: HashMap<String, String> = hashMapOf()
            hashMap["customer"] = customerId
            hashMap["amount"] = "${binding.etAmount.text.toString().toInt() * 100}"
            hashMap["currency"] = "inr"
            hashMap["automatic_payment_methods[enabled]"] = "true"
            val call = apiInterface.getClientSecret(headerMap, hashMap)
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful) {
                        val stringResponse = response.body()!!.string()
                        val jsonObject = JSONObject(stringResponse)
                        clientSecret = jsonObject.optString("client_secret")
                        Toast.makeText(
                            this@StripeActivity,
                            "clientSecret: $clientSecret",
                            Toast.LENGTH_SHORT
                        ).show()

                        paymentFlow()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(
                        this@StripeActivity,
                        "Fail: ${t.printStackTrace()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        } catch (Ex: Exception) {
            Log.e("Error", Ex.localizedMessage)
        }
    }
}