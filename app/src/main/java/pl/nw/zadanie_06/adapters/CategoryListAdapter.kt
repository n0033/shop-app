package pl.nw.zadanie_06.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import pl.nw.zadanie_06.R
import pl.nw.zadanie_06.models.data.Category


class CategoryListAdapter : RecyclerView.Adapter<CategoryListAdapter.ViewHolder>() {
    private var categoryList: List<Category> = listOf()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val categoryName: TextView

        init {
            categoryName = view.findViewById(R.id.category_list_item_name)

        }

    }


    fun setData(categories: List<Category>) {
        this.categoryList = categories;
        notifyDataSetChanged();
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.category, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = categoryList[position]
        holder.categoryName.text = category.name
    }

    override fun getItemCount() = categoryList.size

}
