package pl.nw.zadanie_06.common.local_db

import androidx.room.*
import pl.nw.zadanie_06.models.data.User

@Dao
interface UserDao {
    @Query("SELECT * FROM user WHERE uid LIKE :userId")
    fun findByUserId(userId: String): User

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUsers(vararg users: User)

    @Update
    fun update(vararg users: User)

}