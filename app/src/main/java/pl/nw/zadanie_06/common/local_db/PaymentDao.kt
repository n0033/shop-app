package pl.nw.zadanie_06.common.local_db

import androidx.room.*
import pl.nw.zadanie_06.models.data.Payment

@Dao
interface PaymentDao {

    @Query("SELECT * FROM payment WHERE userId = :userId")
    suspend fun findByUserId(userId: String): List<Payment>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg payments: Payment)

    @Update
    suspend fun update(vararg payment: Payment)
}