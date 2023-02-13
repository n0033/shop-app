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

data class ProductListUiState(
    var productList: List<Product> = listOf(),
    var categoryList: List<Category> = listOf()
)

class ProductListViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ProductListUiState())
    val uiState: StateFlow<ProductListUiState> = _uiState

    init {
        val productReference = RealtimeDatabase<Product>().read(COLLECTION["product"]!!)
        val productListener = object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val products: MutableList<Product> = mutableListOf()
                for (productSnapshot in dataSnapshot.children) {
                    products.add(productSnapshot.getValue<Product>()!!)
                }
                _uiState.update { currentState ->
                    currentState.copy(productList = products)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(ContentValues.TAG, "loadPost:onCancelled", error.toException())
            }
        }

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

        productReference.addValueEventListener(productListener)
        categoryReference.addValueEventListener(categoryListener)
    }

}