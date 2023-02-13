package pl.nw.zadanie_06.models.data

import pl.nw.zadanie_06.models.Deserializable
import pl.nw.zadanie_06.models.Serializable
import java.util.UUID

data class Category(
    override val uid: String = UUID.randomUUID().toString(),
    var name: String? = null
) : EntityBase(), Serializable<Category> {

    override fun toHashMap(): HashMap<String, Any?>{
        return hashMapOf(
            "uid" to uid,
            "name" to name
        )
    }

    companion object : Deserializable<Category> {
        override fun fromHashMap(map: HashMap<String, Any>): Category {
            return Category(
                map["uid"] as String,
                map["name"] as String
            )
        }
    }


}