package pl.nw.zadanie_06.models.view

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

import pl.nw.zadanie_06.common.realtime_db.RealtimeDatabase
import pl.nw.zadanie_06.Constants.COLLECTION
import pl.nw.zadanie_06.models.data.Category

data class CategoryListUiState(
    var categoryList: List<Category> = listOf()
)

class CategoryListViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(CategoryListUiState())
    val uiState: StateFlow<CategoryListUiState> = _uiState

    init {
        val categoryReference = RealtimeDatabase<Category>().read(COLLECTION["category"]!!)
        val categoryListener = object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val categories: MutableList<Category> = mutableListOf()
                for (categorySnapshot in dataSnapshot.children) {
                    categories.add(categorySnapshot.getValue<Category>()!!)
                }
                _uiState.update { currentState ->
                    currentState.copy(categoryList = categories)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(ContentValues.TAG, "loadPost:onCancelled", error.toException())
            }
        }

        categoryReference.addValueEventListener(categoryListener)
    }

}
