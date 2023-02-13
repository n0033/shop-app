package pl.nw.zadanie_06.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import pl.nw.zadanie_06.R
import pl.nw.zadanie_06.adapters.ProductListAdapter
import pl.nw.zadanie_06.databinding.ProductListBinding
import pl.nw.zadanie_06.models.view.ProductListViewModel

class ProductListFragment : Fragment(R.layout.product_list) {

    private var _binding: ProductListBinding? = null;
    private val binding get() = _binding!!
    private val viewModel: ProductListViewModel by viewModels()
    private val adapter: ProductListAdapter = ProductListAdapter {
        findNavController().navigate(
            R.id.action_ProductListFragment_to_ProductDetailsFragment, bundleOf(
                "productId" to it.uid,
                "categoryId" to it.categoryId
            )
        )

    };


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ProductListBinding.inflate(layoutInflater)
        binding.productList.layoutManager = LinearLayoutManager(requireContext())
        binding.productList.adapter = this.adapter;
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleScope.launch {
                viewModel.uiState.collect { value ->
                    adapter.setData(value.productList, value.categoryList)
                }
            }
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null;
    }
}