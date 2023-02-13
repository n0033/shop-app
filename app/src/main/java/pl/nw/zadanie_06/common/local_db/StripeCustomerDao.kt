package pl.nw.zadanie_06.common.local_db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import pl.nw.zadanie_06.models.data.StripeCustomer


@Dao
interface StripeCustomerDao {

    @Query("SELECT * FROM StripeCustomer WHERE userId = :userId")
    suspend fun findByUserId(userId: String): StripeCustomer?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg stripeCustomer: StripeCustomer)
}