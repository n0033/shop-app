package pl.nw.zadanie_06.common.local_db

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import pl.nw.zadanie_06.models.data.Cart

@Dao
interface CartDao {

    @Query("SELECT * FROM cart WHERE userId = :userId")
    suspend fun findCartByUserId(userId: String): Cart?

    @Query("SELECT * FROM cart WHERE userId = :userId")
    fun loadCartByUserId(userId: String): Flow<Cart>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg carts: Cart)

    @Update
    suspend fun update(vararg carts: Cart)
}