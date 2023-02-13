package pl.nw.zadanie_06.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import pl.nw.zadanie_06.R
import pl.nw.zadanie_06.models.data.Category
import pl.nw.zadanie_06.models.data.Product


class ProductListAdapter(private val listener: (Product) -> Unit) :
    RecyclerView.Adapter<ProductListAdapter.ViewHolder>() {
    private var products: List<Product> = listOf()
    private var categories: List<Category> = listOf()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val productName: TextView
        val productPrice: TextView
        val categoryName: TextView

        init {
            productName = view.findViewById(R.id.product_list_item_name)
            productPrice = view.findViewById(R.id.product_list_item_price)
            categoryName = view.findViewById(R.id.product_list_item_category)

        }

    }


    fun setData(products: List<Product>, categories: List<Category>) {
        this.products = products;
        this.categories = categories;
        notifyDataSetChanged();
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.product, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = products[position]
        holder.productName.text = product.name
        holder.productPrice.text = product.price.toString().plus("$")
        val categoriesFiltered = categories.filter { it.uid == product.categoryId }
        if (categoriesFiltered.isNotEmpty()) {
            holder.categoryName.text = categoriesFiltered[0].name
        }
        holder.itemView.setOnClickListener { listener(product) }
    }

    override fun getItemCount() = products.size

}
