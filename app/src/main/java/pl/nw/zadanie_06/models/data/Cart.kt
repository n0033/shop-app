package pl.nw.zadanie_06.models.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import pl.nw.zadanie_06.utils.Converters

data class CartItem (
    var quantity: Int,
    var productId: String
)

data class CartItemList (
    var cartItemList: ArrayList<CartItem>
)

@Entity
@TypeConverters(value = [Converters::class])
data class Cart(
    @PrimaryKey val userId: String,
    val items: CartItemList
)


