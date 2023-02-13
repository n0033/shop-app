package pl.nw.zadanie_06

import com.google.firebase.database.FirebaseDatabase

object Constants {
    val COLLECTION = mapOf(
        "cart" to "carts",
        "category" to "categories",
        "product" to "products",
    )
    val DATABASE_URI = System.getenv("DATABASE_URI")
        ?: "https://android-06-f6858-default-rtdb.europe-west1.firebasedatabase.app"
    val REALTIME_DB = FirebaseDatabase.getInstance(DATABASE_URI).reference
    val LOCAL_DB_FILENAME = System.getenv("LOCAL_DB_FILENAME") ?: "local3.db"

    val STRIPE_PUBLISHABLE_KEY = System.getenv("STRIPE_PUBLISHABLE_KEY")
        ?: "pk_test_51MKLi0AmUR8Uxul1QpPrt8w0e6WqVOoMDB4e61jz3AVERIXeWLIKQOWs4FD71bwnvGqxR1UNUq2bEC1lCmX70F1A00Td2dsTVX"
    val STRIPE_SECRET_KEY = System.getenv("STRIPE_SECRET_KEY")
        ?: "sk_test_51MKLi0AmUR8Uxul17nxySwrxcbbeMkRgF1dtsjgiL7r2nBXVSZWCcIsWaPzYBiCSqfE3OOvvIHWfuexmGwxrFUa600sBEIU2s4"
    val STRIPE_CREATE_CUSTOMER_URL =
        System.getenv("STRIPE_CREATE_CUSTOMER_URL") ?: "https://api.stripe.com/v1/customers"
    val STRIPE_PAYMENT_INTENT_URL =
        System.getenv("STRIPE_PAYMENT_INTENT_URL") ?: "https://api.stripe.com/v1/payment_intents"

    val PAYU_SUCCESS_URL =
        System.getenv("PAYU_SUCCESS_URL")
            ?: ("https://run.mocky.io/v3/9ead34f3-f215-4bc2-bf34-d1794d9a497a")
    val PAYU_FAILURE_URL =
        System.getenv("PAYU_FAILURE_URL")
            ?: ("https://run.mocky.io/v3/bfd0abfe-c3ec-4dd7-a7e8-866ce69b7771")
    val MOCK_MESSAGE_RESPONSE_URL = System.getenv("MOCK_MESSAGE_RESPONSE_URL")
        ?: ("https://run.mocky.io/v3/434c6024-c99f-4df7-a0f9-85a3032c695a")
    val PAYU_KEY = System.getenv("PAYU_KEY") ?: "gtKFFx"
    val PAYU_SALT = System.getenv("PAYU_SALT") ?: "4R38IvwiV57FwVpsgOvTXBdLE4tHUXFW"

}