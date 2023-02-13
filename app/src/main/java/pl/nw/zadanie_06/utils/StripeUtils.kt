package pl.nw.zadanie_06.utils

import com.github.kittinunf.fuel.core.extensions.authentication
import com.github.kittinunf.fuel.gson.responseObject
import com.github.kittinunf.fuel.httpPost
import kotlinx.coroutines.runBlocking
import pl.nw.zadanie_06.Constants
import pl.nw.zadanie_06.common.local_db.LocalDatabase
import pl.nw.zadanie_06.models.data.StripeCustomer

class StripeUtils {

    data class StripeCustomerId(var id: String = "")
    data class StripePaymentIntent(
        var id: String = "", var client_secret: String = "", val customer: String = ""
    )

    companion object {

        suspend fun ensureCustomerExists(db: LocalDatabase, userId: String) {
            val stripeCustomer = db.stripeCustomerDao().findByUserId(userId)

            if (stripeCustomer == null) {
                Constants.STRIPE_CREATE_CUSTOMER_URL.httpPost().authentication()
                    .basic(Constants.STRIPE_SECRET_KEY, "")
                    .responseObject<StripeCustomerId> { _, _, result ->
                        val (body, _) = result
                        val newCustomer =
                            StripeCustomer(userId = userId, stripeCustomerId = body!!.id)
                        runBlocking {
                            db.stripeCustomerDao().insert(newCustomer)
                        }

                    }
            }

        }

    }

}
