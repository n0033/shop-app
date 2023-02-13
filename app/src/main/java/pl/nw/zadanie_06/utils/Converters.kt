package pl.nw.zadanie_06.utils

import androidx.room.TypeConverter
import com.google.gson.Gson
import pl.nw.zadanie_06.models.data.Address
import pl.nw.zadanie_06.models.data.CartItemList
import java.time.LocalDateTime
import java.util.*

class Converters {
    @TypeConverter
    fun fromJSONToCartItemList(json: String): CartItemList {
        return Gson().fromJson(json, CartItemList::class.java)
    }

    @TypeConverter
    fun fromCartItemListToJSON(cartItems: CartItemList) : String {
        return Gson().toJson(cartItems)
    }

    @TypeConverter
    fun fromJSONtoAddress(json: String): Address {
        return Gson().fromJson(json, Address::class.java)
    }

    @TypeConverter
    fun fromAddressToJSON(address: Address) : String {
        return Gson().toJson(address)
    }

    @TypeConverter
    fun toLocalDateTime(dateString: String?): LocalDateTime? {
        return if (dateString == null) {
            null
        } else {
            LocalDateTime.parse(dateString)
        }
    }

    @TypeConverter
    fun fromLocalDateTimeToString(date: LocalDateTime?): String? {
        return date?.toString()
    }


    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}