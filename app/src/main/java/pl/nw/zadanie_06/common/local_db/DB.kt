package pl.nw.zadanie_06.common.local_db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import pl.nw.zadanie_06.models.data.Cart
import pl.nw.zadanie_06.models.data.User
import pl.nw.zadanie_06.Constants
import pl.nw.zadanie_06.models.data.Payment
import pl.nw.zadanie_06.models.data.StripeCustomer


@Database(
    entities = [Cart::class, User::class, Payment::class, StripeCustomer::class], version = 1
)
abstract class LocalDatabase : RoomDatabase() {
    abstract fun cartDao(): CartDao
    abstract fun userDao(): UserDao
    abstract fun paymentDao(): PaymentDao
    abstract fun stripeCustomerDao(): StripeCustomerDao


    companion object {
        private var INSTANCE: LocalDatabase? = null

        fun getInstance(context: Context): LocalDatabase {
            if (INSTANCE == null) {
                synchronized(LocalDatabase::class) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        LocalDatabase::class.java,
                        Constants.LOCAL_DB_FILENAME
                    ).build()
                }
            }
            return INSTANCE!!
        }
    }

}