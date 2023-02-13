package pl.nw.zadanie_06.models

interface Serializable<T> {
    fun toHashMap(): HashMap<String, Any?>;
}

interface Deserializable<T> {
   fun fromHashMap(map: HashMap<String, Any>): T;
}