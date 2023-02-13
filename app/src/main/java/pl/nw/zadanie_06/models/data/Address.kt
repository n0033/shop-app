package pl.nw.zadanie_06.models.data

import androidx.room.Entity
import androidx.room.TypeConverters
import pl.nw.zadanie_06.utils.Converters

@TypeConverters(value = [Converters::class])
data class Address (
    var line1: String,
    var line2: String,
    var postCode: String,
    var city: String
)