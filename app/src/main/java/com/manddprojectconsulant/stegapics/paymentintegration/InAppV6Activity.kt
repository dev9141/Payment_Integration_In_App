package com.manddprojectconsulant.stegapics.paymentintegration

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.android.billingclient.api.*
import com.manddprojectconsulant.stegapics.R

class InAppV6Activity : AppCompatActivity() {

    private lateinit var button: TextView

    private lateinit var subscriptionUtil: SubscriptionUtil
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_in_app)
        subscriptionUtil = SubscriptionUtil(this)
        button = findViewById(R.id.tvPurchase)

        val skuList = ArrayList<String>()
        skuList.add("com.stano.billing")

        val purchasesUpdatedListener = PurchasesUpdatedListener{
                billingResult, purchses ->
        }

        var billingClient = BillingClient.newBuilder(this)
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases().build()

        button.setOnClickListener {

            subscriptionUtil.launchBilling()
        }
    }
}