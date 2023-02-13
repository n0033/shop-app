package pl.nw.zadanie_06.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import pl.nw.zadanie_06.R
import pl.nw.zadanie_06.models.data.CartItem

import pl.nw.zadanie_06.models.data.Product


class OrderAdapter : RecyclerView.Adapter<OrderAdapter.ViewHolder>() {
    private lateinit var cartItems: ArrayList<CartItem>
    private var products: MutableMap<String, Product> = mutableMapOf()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val productName: TextView
        val quantity: TextView
        val totalPrice: TextView
        init {
            productName = view.findViewById(R.id.order_item_name)
            quantity = view.findViewById(R.id.order_item_quantity)
            totalPrice = view.findViewById(R.id.order_item_price)
        }
    }

    fun setData(cartItems: ArrayList<CartItem>, products: MutableMap<String, Product>) {
        this.cartItems = cartItems
        this.products = products
        notifyDataSetChanged();
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.order_item, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cartItem = cartItems[position]
        val product = products[cartItem.productId]!!
        val quantity = cartItem.quantity
        holder.productName.text = product.name
        holder.quantity.text = quantity.toString()
        holder.totalPrice.text = "%.2f $".format(product.price!! * quantity)
    }

    override fun getItemCount() = cartItems.size

}
