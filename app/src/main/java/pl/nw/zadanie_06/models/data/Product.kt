package pl.nw.zadanie_06.models.data

import pl.nw.zadanie_06.models.Deserializable
import pl.nw.zadanie_06.models.Serializable
import java.util.UUID

data class Product(
    override val uid: String = UUID.randomUUID().toString(),
    var name: String? = null,
    var description: String? = null,
    var price: Double? = null,
    var categoryId: String? = null
) : EntityBase(), Serializable<Product> {

    override fun toHashMap(): HashMap<String, Any?> {
        return hashMapOf(
            "uid" to uid,
            "name" to name,
            "description" to description,
            "price" to price,
            "category" to categoryId
        )
    }


    companion object : Deserializable<Product> {
        override fun fromHashMap(map: HashMap<String, Any>): Product {
            return Product(
                map["uid"] as String,
                map["name"] as String,
                map["description"] as String,
                map["price"] as Double,
                map["categoryId"] as String
            )
        }
    }

}