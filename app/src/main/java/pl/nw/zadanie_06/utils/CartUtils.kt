package pl.nw.zadanie_06.utils

import com.google.firebase.database.ktx.getValue
import pl.nw.zadanie_06.Constants
import pl.nw.zadanie_06.Constants.COLLECTION
import pl.nw.zadanie_06.common.local_db.LocalDatabase
import pl.nw.zadanie_06.models.data.Cart
import pl.nw.zadanie_06.models.data.CartItem
import pl.nw.zadanie_06.models.data.Product
import pl.nw.zadanie_06.common.realtime_db.RealtimeDatabase
import pl.nw.zadanie_06.models.data.CartItemList

class CartUtils {

    companion object {

        suspend fun createEmptyCart(db: LocalDatabase, userId: String): Cart {
            val cart = Cart(userId, CartItemList(arrayListOf()))
            db.cartDao().insert(cart)
            return cart
        }

        suspend fun addToCart(db: LocalDatabase, product: Product, userId: String) {
            var cart = db.cartDao().findCartByUserId(userId)
            if (cart == null) {
                cart = createEmptyCart(db, userId)
            }
            val existingProducts = cart.items.cartItemList.filter { it.productId == product.uid }
            if (existingProducts.isEmpty()) {
                val item = CartItem(1, product.uid)
                cart.items.cartItemList.add(item)
            }

            if (existingProducts.isNotEmpty()) {
                val item = existingProducts[0]
                item.quantity += 1
            }
            db.cartDao().update(cart)
        }


        suspend fun removeFromCart(db: LocalDatabase, product: Product, userId: String) {
            var cart = db.cartDao().findCartByUserId(userId)
            if (cart == null) {
                cart = createEmptyCart(db, userId)
            }
            val existingProducts = cart.items.cartItemList.filter { it.productId == product.uid }

            if (existingProducts.isEmpty()) {
                return
            }

            val item = existingProducts[0]
            item.quantity -= 1

            if (item.quantity <= 0) {
                cart.items.cartItemList.remove(item)
            }
            db.cartDao().update(cart)
        }


        suspend fun flushCart(db: LocalDatabase, userId: String) {
            var cart = db.cartDao().findCartByUserId(userId)
            if (cart == null) {
                createEmptyCart(db, userId)
                return
            }
            cart.items.cartItemList = arrayListOf()

            db.cartDao().update(cart)
        }
    }
}