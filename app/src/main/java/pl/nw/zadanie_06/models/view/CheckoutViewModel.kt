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
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pl.nw.zadanie_06.Constants
import pl.nw.zadanie_06.common.local_db.LocalDatabase
import pl.nw.zadanie_06.common.realtime_db.RealtimeDatabase
import pl.nw.zadanie_06.models.data.Cart
import pl.nw.zadanie_06.models.data.CartItem
import pl.nw.zadanie_06.models.data.CartItemList
import pl.nw.zadanie_06.models.data.Product

data class CheckoutUiState(
    var products: List<Product> = listOf(),
    var cartItems: List<CartItem> = listOf(),
    var price: Double = 0.0
)

class CheckoutViewModel(db: LocalDatabase, userId: String) : ViewModel() {
    private val _uiState = MutableStateFlow(CheckoutUiState())
    val uiState: StateFlow<CheckoutUiState> = _uiState

    init {
        viewModelScope.launch {
            var price = 0.0
            var cart = db.cartDao().findCartByUserId(userId)

            // ensure cart for given user exists
            if (cart == null) {
                cart = Cart(userId, CartItemList(arrayListOf()))
                db.cartDao().insert(cart)
            }
            val cartItems = db.cartDao().findCartByUserId(userId)!!.items.cartItemList

            val productReference =
                RealtimeDatabase<Product>().read(Constants.COLLECTION["product"]!!)
            val productListener = object : ValueEventListener {

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val cartProducts = mutableListOf<Product>()
                    var tempProduct: Product?
                    for (productSnapshot in dataSnapshot.children) {
                        tempProduct = productSnapshot.getValue<Product>()
                        if (tempProduct == null) continue;
                        cartProducts.add(tempProduct)
                    }

                    _uiState.update { currentState ->
                        cartItems.forEach {
                            val product = cartProducts.filter { product ->
                                product.uid == it.productId
                            }[0]
                            price += product.price!! * it.quantity
                        }
                        currentState.copy(cartItems = cartItems, products = cartProducts, price = price)
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
            return CheckoutViewModel(db, userId) as T
        }
    }
}