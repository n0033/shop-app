package pl.nw.zadanie_06.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import pl.nw.zadanie_06.R
import pl.nw.zadanie_06.adapters.CategoryListAdapter
import pl.nw.zadanie_06.databinding.CategoryListBinding
import pl.nw.zadanie_06.databinding.ProductListBinding
import pl.nw.zadanie_06.models.view.CategoryListViewModel

class CategoryListFragment: Fragment(R.layout.category_list) {

    private var _binding: CategoryListBinding? = null;
    private val binding get() = _binding!!
    private val viewModel: CategoryListViewModel by viewModels()
    private val adapter: CategoryListAdapter = CategoryListAdapter();


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CategoryListBinding.inflate(layoutInflater)
        binding.categoryList.layoutManager = LinearLayoutManager(requireContext())
        binding.categoryList.adapter = this.adapter;
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleScope.launch {
                viewModel.uiState.collect { value ->
                    adapter.setData(value.categoryList)
                }
            }
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null;
    }
}
