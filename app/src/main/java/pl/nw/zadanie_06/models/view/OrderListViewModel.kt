package pl.nw.zadanie_06.models.view

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pl.nw.zadanie_06.Constants
import pl.nw.zadanie_06.common.local_db.LocalDatabase
import pl.nw.zadanie_06.common.realtime_db.RealtimeDatabase
import pl.nw.zadanie_06.models.data.Payment
import pl.nw.zadanie_06.models.data.Product

data class OrderListUiState(
    var payments: List<Payment> = listOf(),
    var products: MutableMap<String, Product> = mutableMapOf()
)

class OrderListViewModel(db: LocalDatabase, userId: String) : ViewModel() {
    private val _uiState = MutableStateFlow(OrderListUiState())
    val uiState: StateFlow<OrderListUiState> = _uiState

    init {
        viewModelScope.launch {
            val payments: List<Payment> = db.paymentDao().findByUserId(userId)
            val productReference =
                RealtimeDatabase<Product>().read(Constants.COLLECTION["product"]!!)
            val productListener = object : ValueEventListener {

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    var tempProduct: Product?
                    for (productSnapshot in dataSnapshot.children) {
                        tempProduct = productSnapshot.getValue<Product>()
                        if (tempProduct == null) continue;
                        _uiState.update { currentState ->
                            val tempMap = currentState.products
                            tempMap[tempProduct.uid] = tempProduct
                            currentState.copy(payments = payments, products = tempMap)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w(ContentValues.TAG, "loadPost:onCancelled", error.toException())
                }
            }
            productReference.addValueEventListener(productListener)
        }
    }

    class Factory(private val db: LocalDatabase, private val userId: String) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return OrderListViewModel(db, userId) as T
        }
    }
}