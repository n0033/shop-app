package pl.nw.zadanie_06.common.realtime_db

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import pl.nw.zadanie_06.Constants
import pl.nw.zadanie_06.models.Serializable

class RealtimeDatabase<T: Serializable<T>> : Repository<T> {

    private val db = Constants.REALTIME_DB;

    override fun create(query: String, data: T): Task<Void> {
        return this.db.child(query).setValue(data)
    }

    override fun read(query: String): DatabaseReference {
        return this.db.child(query)
    }

    override fun update(query: String, data: T): Task<Void> {
        val updateData = hashMapOf<String, Any> (
            "$query" to data.toHashMap()
                )
        return this.db.updateChildren(updateData)
    }

    override fun delete(query: String): Task<Void> {
        return this.db.child(query).removeValue()
    }

}

