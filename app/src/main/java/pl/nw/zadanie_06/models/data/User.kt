package pl.nw.zadanie_06.models.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey val uid: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "email") val email: String,
)