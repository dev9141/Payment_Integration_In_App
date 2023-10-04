package com.manddprojectconsulant.stegapics.paymentintegration

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.android.billingclient.api.*
import com.manddprojectconsulant.stegapics.R

class InAppActivity : AppCompatActivity() {

    private lateinit var button: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_in_app)

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

            billingClient.startConnection(object : BillingClientStateListener{
                override fun onBillingServiceDisconnected() {
//                    TODO("Not yet implemented")
                }

                override fun onBillingSetupFinished(billingResult: BillingResult) {
//                    TODO("Not yet implemented")

                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK){

                        val params = SkuDetailsParams.newBuilder()
                        params.setSkusList(skuList)
                            .setType(BillingClient.SkuType.INAPP)

                        billingClient.querySkuDetailsAsync(params.build()){
                                billingResult, skuDetailsList ->
                            val skuDetails = skuDetailsList!![0]
                            //for (skuDetails in skuDetailsList!!) {
                                val flowPurchase = BillingFlowParams.newBuilder()
                                    .setSkuDetails(skuDetails)
                                    .build()

                                val responseCode = billingClient.launchBillingFlow(this@InAppActivity, flowPurchase).responseCode
                            val resC = responseCode
                            //}
                        }
                    }
                }

            })
        }
    }
}