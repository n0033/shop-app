package pl.nw.zadanie_06.models.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import pl.nw.zadanie_06.utils.Converters
import java.time.LocalDateTime


@Entity
@TypeConverters(value = [Converters::class])
data class Payment(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    var userId: String,
    var timestamp: LocalDateTime,
    var amount: Int,
    var items: CartItemList,
    var address: Address
)