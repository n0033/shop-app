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
import kotlinx.coroutines.runBlocking
import pl.nw.zadanie_06.Constants.COLLECTION
import pl.nw.zadanie_06.models.data.CartItem
import pl.nw.zadanie_06.models.data.Product
import pl.nw.zadanie_06.common.local_db.LocalDatabase
import pl.nw.zadanie_06.common.realtime_db.RealtimeDatabase
import pl.nw.zadanie_06.models.data.Cart
import pl.nw.zadanie_06.models.data.CartItemList

data class CartUiState(
    var items: List<CartItem> = listOf(), var products: List<Product> = listOf()
)

class CartViewModel(db: LocalDatabase, userId: String) : ViewModel() {
    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState

    init {
        viewModelScope.launch {
            var cart = db.cartDao().findCartByUserId(userId)
            // ensure cart for given user exists
            if (cart == null) {
                cart = Cart(userId, CartItemList(arrayListOf()))
                db.cartDao().insert(cart)
            }
            val cartFlow = db.cartDao().loadCartByUserId(userId)
            cartFlow.collect { cartState ->
                _uiState.update { currentState -> currentState.copy(items = cartState.items.cartItemList) }
            }
        }

        val productReference = RealtimeDatabase<Product>().read(COLLECTION["product"]!!)
        val productListener = object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val cartProducts = mutableListOf<Product>();
                var tempProduct: Product?
                for (productSnapshot in dataSnapshot.children) {
                    tempProduct = productSnapshot.getValue<Product>()
                    if (tempProduct == null) continue;
                    cartProducts.add(tempProduct)
                }
                _uiState.update { currentState -> currentState.copy(products = cartProducts) }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(ContentValues.TAG, "loadPost:onCancelled", error.toException())
            }
        }

        productReference.addValueEventListener(productListener);

    }

    class Factory(private val db: LocalDatabase, private val userId: String) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return CartViewModel(db, userId) as T
        }
    }

}