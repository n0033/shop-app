package pl.nw.zadanie_06.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import pl.nw.zadanie_06.R
import pl.nw.zadanie_06.common.local_db.LocalDatabase
import pl.nw.zadanie_06.models.data.CartItem
import pl.nw.zadanie_06.models.data.Product
import kotlin.reflect.KSuspendFunction3


class CartAdapter(
    private val db: LocalDatabase,
    private val userId: String,
    private val removeItemListener: KSuspendFunction3<LocalDatabase, Product, String, Unit>,
    private val addItemListener: KSuspendFunction3<LocalDatabase, Product, String, Unit>,
) : RecyclerView.Adapter<CartAdapter.ViewHolder>() {
    private var cartItems: List<CartItem> = listOf()
    private var products: List<Product> = listOf()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemName: TextView
        val itemQuantity: TextView
        val removeItemSymbol: TextView
        val addItemSymbol: TextView

        init {
            itemName = view.findViewById(R.id.cart_item_name)
            itemQuantity = view.findViewById(R.id.cart_item_quantity)
            removeItemSymbol = view.findViewById(R.id.cart_item_remove)
            addItemSymbol = view.findViewById(R.id.cart_item_add)

        }

    }


    fun setData(products: List<Product>, cartItems: List<CartItem>) {
        this.cartItems = cartItems;
        this.products = products;
        notifyDataSetChanged();
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(viewGroup.context).inflate(R.layout.cart_item, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cartItem = cartItems[position]
        val products = products.filter { it.uid == cartItem.productId }
        if (products.isEmpty()) {
            return
        }

        val product = products[0]
        holder.itemName.text = product.name
        holder.itemQuantity.text = cartItem.quantity.toString();
        holder.removeItemSymbol.setOnClickListener {
            runBlocking {
                launch {
                    removeItemListener(
                        db, product, userId
                    )
                }
            }
        }

        holder.addItemSymbol.setOnClickListener {
            runBlocking {
                launch {
                    addItemListener(db, product, userId)
                }
            }
        }
    }

    override fun getItemCount() = cartItems.size

}
