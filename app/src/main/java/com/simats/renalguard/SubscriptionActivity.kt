package com.simats.renalguard

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.billingclient.api.*
import com.google.android.material.button.MaterialButton
import com.simats.renalguard.LoginActivity
import com.simats.renalguard.R

class SubscriptionActivity : AppCompatActivity(), PurchasesUpdatedListener {

    private lateinit var btnSubscribe: MaterialButton
    private lateinit var btnSkipForNow: MaterialButton
    private lateinit var billingClient: BillingClient
    private var productDetails: ProductDetails? = null

    companion object {
        private const val TAG = "RenalGuardSubscription"
        private const val SUBSCRIPTION_SKU = "renalguard_premium"
        private const val TEST_SUBSCRIPTION_SKU = "android.test.purchased"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subscription)

        btnSubscribe = findViewById(R.id.btnSubscribe)
        btnSkipForNow = findViewById(R.id.btnSkipForNow)

        setupBillingClient()

        btnSubscribe.setOnClickListener { launchSubscriptionFlow() }

        btnSkipForNow.setOnClickListener {
            // Skip goes to login
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun setupBillingClient() {
        billingClient = BillingClient.newBuilder(this)
            .setListener(this)
            .enablePendingPurchases()
            .build()

        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    Log.d(TAG, "✅ Billing setup successful")
                    querySubscriptionDetails()
                } else {
                    Log.e(TAG, "❌ Billing setup failed: ${billingResult.debugMessage}")
                }
            }

            override fun onBillingServiceDisconnected() {
                Log.d(TAG, "⚠️ Billing service disconnected")
            }
        })
    }

    private fun querySubscriptionDetails() {
        querySpecificProduct(SUBSCRIPTION_SKU, BillingClient.ProductType.SUBS) { success ->
            if (!success) {
                Log.w(TAG, "Real subscription not found, trying test SKU...")
                querySpecificProduct(TEST_SUBSCRIPTION_SKU, BillingClient.ProductType.INAPP) {}
            }
        }
    }

    private fun querySpecificProduct(productId: String, productType: String, callback: (Boolean) -> Unit) {
        val productList = listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(productId)
                .setProductType(productType)
                .build()
        )

        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()

        billingClient.queryProductDetailsAsync(params) { billingResult, productDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                if (productDetailsList.isNotEmpty()) {
                    productDetails = productDetailsList[0]
                    Log.d(TAG, "✅ Product found: $productId")
                    callback(true)
                } else callback(false)
            } else callback(false)
        }
    }

    private fun launchSubscriptionFlow() {
        if (!billingClient.isReady) {
            Toast.makeText(this, "Billing not ready. Try again.", Toast.LENGTH_SHORT).show()
            return
        }

        if (productDetails != null) {
            val productDetailsParamsList = if (productDetails!!.productType == BillingClient.ProductType.SUBS) {
                val offerDetails = productDetails!!.subscriptionOfferDetails
                if (offerDetails.isNullOrEmpty()) {
                    Toast.makeText(this, "No subscription offers found", Toast.LENGTH_SHORT).show()
                    return
                }
                val selectedOffer = offerDetails[0]
                listOf(
                    BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(productDetails!!)
                        .setOfferToken(selectedOffer.offerToken)
                        .build()
                )
            } else {
                listOf(
                    BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(productDetails!!)
                        .build()
                )
            }

            billingClient.launchBillingFlow(
                this,
                BillingFlowParams.newBuilder()
                    .setProductDetailsParamsList(productDetailsParamsList)
                    .build()
            )
        } else {
            Toast.makeText(this, "Subscription not available.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: MutableList<Purchase>?) {
        when (billingResult.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                purchases?.forEach { handlePurchase(it) }
            }
            BillingClient.BillingResponseCode.USER_CANCELED -> {
                Toast.makeText(this, "Purchase canceled", Toast.LENGTH_SHORT).show()
            }
            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> {
                Toast.makeText(this, "You already have an active subscription", Toast.LENGTH_SHORT).show()
                navigateToLogin()
            }
            else -> {
                Toast.makeText(this, "Purchase failed: ${billingResult.debugMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED && !purchase.isAcknowledged) {
            val acknowledgeParams = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()

            billingClient.acknowledgePurchase(acknowledgeParams) { result ->
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    onSubscriptionSuccess()
                }
            }
        } else if (purchase.isAcknowledged) {
            onSubscriptionSuccess()
        }
    }

    private fun onSubscriptionSuccess() {
        Toast.makeText(this, "Subscription successful! Welcome to RenalGuard Premium.", Toast.LENGTH_LONG).show()

        val sharedPref = getSharedPreferences("renalguard_subscription", MODE_PRIVATE)
        with(sharedPref.edit()) {
            putBoolean("is_premium_user", true)
            putLong("subscription_time", System.currentTimeMillis())
            apply()
        }

        navigateToLogin()
    }

    private fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::billingClient.isInitialized) {
            billingClient.endConnection()
        }
    }
}
