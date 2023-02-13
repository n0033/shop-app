package pl.nw.zadanie_06.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.webkit.WebView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.github.kittinunf.fuel.core.extensions.authentication
import com.github.kittinunf.fuel.gson.responseObject
import com.github.kittinunf.fuel.httpPost
import com.google.firebase.auth.FirebaseAuth
import com.payu.base.models.ErrorResponse
import com.payu.base.models.PayUPaymentParams
import com.payu.checkoutpro.PayUCheckoutPro
import com.payu.checkoutpro.utils.PayUCheckoutProConstants
import com.payu.checkoutpro.utils.PayUCheckoutProConstants.CP_HASH_NAME
import com.payu.checkoutpro.utils.PayUCheckoutProConstants.CP_HASH_STRING
import com.payu.ui.model.listeners.PayUCheckoutProListener
import com.payu.ui.model.listeners.PayUHashGenerationListener
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import pl.nw.zadanie_06.Constants
import pl.nw.zadanie_06.MainActivity
import pl.nw.zadanie_06.common.local_db.LocalDatabase
import pl.nw.zadanie_06.databinding.ActivityCheckoutBinding
import pl.nw.zadanie_06.models.data.Address
import pl.nw.zadanie_06.models.data.Payment
import pl.nw.zadanie_06.models.view.CheckoutViewModel
import pl.nw.zadanie_06.utils.CartUtils
import pl.nw.zadanie_06.utils.StripeUtils
import pl.nw.zadanie_06.utils.UserUtils
import java.math.BigInteger
import java.security.MessageDigest
import java.time.LocalDateTime
import java.util.*

class CheckoutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCheckoutBinding
    private lateinit var db: LocalDatabase
    private lateinit var auth: FirebaseAuth
    private val user = FirebaseAuth.getInstance().currentUser
    private val viewModel: CheckoutViewModel by viewModels {
        val db = LocalDatabase.getInstance(applicationContext)
        UserUtils.ensureAuth(this)
        CheckoutViewModel.Factory(db, user!!.uid)
    }
    private lateinit var payment: Payment
    lateinit var paymentIntentClientSecret: String
    lateinit var customerConfig: PaymentSheet.CustomerConfiguration
    lateinit var paymentSheet: PaymentSheet

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        paymentSheet = PaymentSheet(this, ::onPaymentSheetResult)
        db = LocalDatabase.getInstance(applicationContext)

        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var price = 0.0
        binding.apply {
            lifecycleScope.launch {
                viewModel.uiState.collect { checkoutState ->
                    price = checkoutState.price
                    binding.checkoutTotalPrice.text = "%.2f $".format(checkoutState.price)
                }
            }
        }

        binding.payWithStripeButton.setOnClickListener {
            val address = Address(
                line1 = binding.checkoutAddress1Input.text.toString(),
                line2 = binding.checkoutAddress2Input.text.toString(),
                postCode = binding.checkoutPostCodeInput.text.toString(),
                city = binding.checkoutCityInput.text.toString()
            )
            runBlocking {
                val cartItems = db.cartDao().findCartByUserId(user!!.uid)!!.items
                payment = Payment(
                    userId = user.uid,
                    timestamp = LocalDateTime.now(),
                    amount = (price * 100).toInt(),
                    items = cartItems,
                    address = address
                )
                val stripeCustomer = db.stripeCustomerDao().findByUserId(user.uid)!!
                Constants.STRIPE_PAYMENT_INTENT_URL.httpPost(
                    listOf(
                        "customer" to stripeCustomer.stripeCustomerId,
                        "amount" to payment.amount,
                        "currency" to "usd",
                        "automatic_payment_methods[enabled]" to true
                    )
                ).authentication().basic(Constants.STRIPE_SECRET_KEY, "")
                    .responseObject<StripeUtils.StripePaymentIntent> { _, _, result ->
                        val (body, _) = result
                        paymentIntentClientSecret = body!!.client_secret
                        PaymentConfiguration.init(
                            applicationContext, Constants.STRIPE_PUBLISHABLE_KEY
                        )
                        presentPaymentSheet()
                    }
            }

        }

        binding.payWithPayuButton.setOnClickListener {
            val address = Address(
                line1 = binding.checkoutAddress1Input.text.toString(),
                line2 = binding.checkoutAddress2Input.text.toString(),
                postCode = binding.checkoutPostCodeInput.text.toString(),
                city = binding.checkoutCityInput.text.toString()
            )
            runBlocking {
                val cartItems = db.cartDao().findCartByUserId(user!!.uid)!!.items
                payment = Payment(
                    userId = user.uid,
                    timestamp = LocalDateTime.now(),
                    amount = (price * 100).toInt(),
                    items = cartItems,
                    address = address
                )
                val payUPaymentParams = PayUPaymentParams.Builder()
                    .setAmount(price.toString())
                    .setIsProduction(false)
                    .setKey(Constants.PAYU_KEY)
                    .setProductInfo("Norb shop order")
                    .setTransactionId(UUID.randomUUID().toString())
                    .setFirstName(user.displayName)
                    .setEmail(user.email)
                    .setSurl(Constants.PAYU_SUCCESS_URL)
                    .setFurl(Constants.PAYU_FAILURE_URL)
                    .setPhone("8160651749")
                    .build()
                println(Constants.PAYU_SUCCESS_URL)

                PayUCheckoutPro.open(
                    this@CheckoutActivity, payUPaymentParams, object : PayUCheckoutProListener {

                        override fun onPaymentSuccess(response: Any) {
                            response as HashMap<*, *>
                            val payUResponse = response[PayUCheckoutProConstants.CP_PAYU_RESPONSE]
                            val merchantResponse = response[PayUCheckoutProConstants.CP_MERCHANT_RESPONSE]
                            runBlocking {
                                db.paymentDao().insert(payment)
                                CartUtils.flushCart(db, user.uid)
                            }
                            val intent = Intent(this@CheckoutActivity, MainActivity::class.java)
                            startActivity(intent)
                        }


                        override fun onPaymentFailure(response: Any) {
                            response as HashMap<*, *>
                            println("payment failure")
                            val payUResponse = response[PayUCheckoutProConstants.CP_PAYU_RESPONSE]
                            val merchantResponse = response[PayUCheckoutProConstants.CP_MERCHANT_RESPONSE]
                        }


                        override fun onPaymentCancel(isTxnInitiated:Boolean) {
                        }


                        override fun onError(errorResponse: ErrorResponse) {
                            println("error")
                            println(errorResponse.errorMessage)
                            println(errorResponse.errorCode)
                        }

                        override fun setWebViewProperties(webView: WebView?, bank: Any?) {
                        }

                        override fun generateHash(
                            valueMap: HashMap<String, String?>,
                            hashGenerationListener: PayUHashGenerationListener
                        ) {
                            if ( valueMap.containsKey(CP_HASH_STRING)
                                && valueMap.containsKey(CP_HASH_STRING) != null
                                && valueMap.containsKey(CP_HASH_NAME)
                                && valueMap.containsKey(CP_HASH_NAME) != null) {

                                val hashData = valueMap[CP_HASH_STRING]
                                val hashName = valueMap[CP_HASH_NAME]
                                val md = MessageDigest.getInstance("SHA-512")
                                println("hashdata: $hashData")
                                val messageDigest = md.digest("$hashData${Constants.PAYU_SALT}".toByteArray())
                                val no = BigInteger(1, messageDigest)
                                var hashtext = no.toString(16)
                                while (hashtext.length < 32) {
                                    hashtext = "0$hashtext"
                                }
                                val hash: String = hashtext
                                println("hash: $hash")
                                if (!TextUtils.isEmpty(hash)) {
                                    val dataMap: HashMap<String, String?> = HashMap()
                                    dataMap[hashName!!] = hash
                                    hashGenerationListener.onHashGenerated(dataMap)
                                }
                            }
                        }
                    })
            }
        }
    }

    private fun presentPaymentSheet() {
        println("paymentinent: $paymentIntentClientSecret")
        paymentSheet.presentWithPaymentIntent(
            paymentIntentClientSecret, PaymentSheet.Configuration(
                merchantDisplayName = "Norbert shop", allowsDelayedPaymentMethods = true
            )
        )
    }

    private fun onPaymentSheetResult(paymentSheetResult: PaymentSheetResult) {
        println(paymentSheetResult)
        when (paymentSheetResult) {
            is PaymentSheetResult.Canceled -> {
                print("Canceled")
            }
            is PaymentSheetResult.Failed -> {
                print("Error: ${paymentSheetResult.error}")
            }
            is PaymentSheetResult.Completed -> {
                runBlocking {
                    db.paymentDao().insert(payment)
                    CartUtils.flushCart(db, user!!.uid)
                }
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }
    }

}