package com.manddprojectconsulant.stegapics.paymentintegration

import android.app.Activity
import android.util.Log
import com.android.billingclient.api.*
import com.google.common.collect.ImmutableList


//Changes by HB -20/03/2023 subscription flow
class SubscriptionUtil(context: Activity) {
    private lateinit var billingClient: BillingClient
    private lateinit var productDetails: ProductDetails
    private lateinit var purchase: Purchase
    private val productID = "android.test.purchased"
    var IS_USER_SUBSCRIBE = false
    private var mContext: Activity = context

    fun launchBilling() {
        //if (this::productDetails.isInitialized) {
            val billingFlowParams = BillingFlowParams.newBuilder()
                    .setProductDetailsParamsList(
                            ImmutableList.of(
                                    BillingFlowParams.ProductDetailsParams.newBuilder()
                                            .setProductDetails(productDetails)
                                            /*.setOfferToken(
                                                    productDetails.subscriptionOfferDetails?.get(
                                                            0
                                                    )?.offerToken!!
                                            )*/
                                            .build()
                            )
                    )
                    .build()
            val rCode = billingClient.launchBillingFlow(mContext, billingFlowParams).responseCode
        val rc= rCode
       /* } else {
            Toast.makeText(mContext, "no product found", Toast.LENGTH_SHORT).show()
        }*/
    }

    //Changes by HB-06/02/2023 setup for subscriptions
    private fun billingSetup() {
        billingClient = BillingClient.newBuilder(mContext)
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases()
                .build()

        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(
                    billingResult: BillingResult
            ) {
                if (billingResult.responseCode ==
                        BillingClient.BillingResponseCode.OK
                ) {
                    //Log.i(TAG, "OnBillingSetupFinish connected")
                    queryProduct(productID)
                    reloadPurchase()
                } else {
                    Log.d("SubscriptionUtil", "OnBillingSetupFinish failed")
                }
            }

            override fun onBillingServiceDisconnected() {
                Log.d("SubscriptionUtil", "OnBillingSetupFinish connection lost")
            }
        })
    }

    //Changes by HB-06/02/2023 Purchase update once done
    private val purchasesUpdatedListener =
            PurchasesUpdatedListener { billingResult, purchases ->
                if (billingResult.responseCode ==
                        BillingClient.BillingResponseCode.OK
                        && purchases != null
                ) {
                    for (purchase in purchases) {
                        completePurchase(purchase)
                    }
                } else if (billingResult.responseCode ==
                        BillingClient.BillingResponseCode.USER_CANCELED
                ) {
                    Log.i("SubscriptionUtil", "onPurchasesUpdated: Purchase Canceled")
                } else {
                    Log.i("SubscriptionUtil", "onPurchasesUpdated: Error")
                }
            }

    //Changes by HB-06/02/2023 Purchase Complete
    private fun completePurchase(item: Purchase) {
        purchase = item
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            IS_USER_SUBSCRIBE = true
        }
    }

    //Changes by HB-06/02/2023 Get the product
    private fun queryProduct(productId: String) {
        val queryProductDetailsParams = QueryProductDetailsParams.newBuilder()
                .setProductList(
                        ImmutableList.of(
                                QueryProductDetailsParams.Product.newBuilder()
                                        .setProductId(productId)
                                        .setProductType(
                                                BillingClient.ProductType.INAPP
                                        )
                                        .build()
                        )
                )
                .build()

        billingClient.queryProductDetailsAsync(
                queryProductDetailsParams
        ) { billingResult, productDetailsList ->
            if (productDetailsList.isNotEmpty()) {
                productDetails = productDetailsList[0]
            } else {
                Log.d("SubscriptionUtil", "onProductDetailsResponse: No products")
            }
        }
    }

    //Changes by HB-06/02/2023 reload purchase
    private fun reloadPurchase() {
        val queryPurchasesParams = QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.INAPP)
                .build()

        billingClient.queryPurchasesAsync(
                queryPurchasesParams,
                purchasesListener
        )
    }

    private val purchasesListener =
            PurchasesResponseListener { billingResult, purchases ->
                if (purchases.isNotEmpty()) {
                    purchase = purchases.first()
                    // binding.consumeButton.isEnabled = true
                    Log.d("Consume Purchase", "true")
                    IS_USER_SUBSCRIBE = true
                } else {
                    // TODO: need to change false
                    IS_USER_SUBSCRIBE = true
                }
            }

    init {
        billingSetup()
    }
}