package pl.nw.zadanie_06.common.realtime_db


interface Repository<T> {

    fun create(query: String, data: T) : Any

    fun read(query: String): Any

    fun update(query: String, data: T) : Any

    fun delete(query: String) : Any
}