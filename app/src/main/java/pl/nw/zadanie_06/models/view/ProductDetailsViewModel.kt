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

import pl.nw.zadanie_06.models.data.Product
import pl.nw.zadanie_06.common.realtime_db.RealtimeDatabase
import pl.nw.zadanie_06.Constants.COLLECTION
import pl.nw.zadanie_06.models.data.Category

data class ProductDetailsUiState(
    var product: Product? = null,
    var category: Category? = null
)

class ProductDetailsViewModel(productId: String, categoryId: String) : ViewModel() {
    private val _uiState = MutableStateFlow(ProductDetailsUiState())
    val uiState: StateFlow<ProductDetailsUiState> = _uiState

    init {
        val productReference =
            RealtimeDatabase<Product>().read("${COLLECTION["product"]!!}/${productId}")
        val productListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val product = dataSnapshot.getValue<Product>()!!
                _uiState.update { currentState ->
                    currentState.copy(product = product)
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(ContentValues.TAG, "loadPost:onCancelled", error.toException())
            }
        }


        val categoryReference =
            RealtimeDatabase<Category>().read("${COLLECTION["category"]!!}/${categoryId}")
        val categoryListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val category = dataSnapshot.getValue<Category>()!!
                _uiState.update { currentState ->
                    currentState.copy(category = category)
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(ContentValues.TAG, "loadPost:onCancelled", error.toException())
            }
        }

        productReference.addValueEventListener(productListener)
        categoryReference.addValueEventListener(categoryListener)
    }

}