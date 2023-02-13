package pl.nw.zadanie_06.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import pl.nw.zadanie_06.R
import pl.nw.zadanie_06.models.data.Payment
import pl.nw.zadanie_06.models.data.Product
import java.time.format.DateTimeFormatter


class OrderListAdapter() :
    RecyclerView.Adapter<OrderListAdapter.ViewHolder>() {
    private var payments: List<Payment> = listOf()
    private var products: MutableMap<String, Product> = mutableMapOf()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val paymentDate: TextView
        val totalPrice: TextView
        val orderItemList: RecyclerView

        init {
            paymentDate = view.findViewById(R.id.order_item_date)
            totalPrice = view.findViewById(R.id.order_item_total_price)
            orderItemList = view.findViewById(R.id.order_items_recycler)
        }

    }


    fun setData(payments: List<Payment>, products: MutableMap<String, Product>) {
        this.payments = payments
        this.products = products
        notifyDataSetChanged();
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.order_list, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val payment = payments[position]
        val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        holder.paymentDate.text = payment.timestamp.format(dateFormat)
        holder.totalPrice.text = (payment.amount.toDouble() / 100).toString().plus("$")
        holder.orderItemList.layoutManager =
            LinearLayoutManager(holder.itemView.context, LinearLayoutManager.VERTICAL, false)
        val adapter = OrderAdapter()
        adapter.setData(cartItems=payment.items.cartItemList, products=products)
        holder.orderItemList.adapter = adapter
    }

    override fun getItemCount() = payments.size

}
