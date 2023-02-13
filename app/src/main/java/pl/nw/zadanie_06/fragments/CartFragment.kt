package pl.nw.zadanie_06.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import pl.nw.zadanie_06.R
import pl.nw.zadanie_06.activities.CheckoutActivity
import pl.nw.zadanie_06.adapters.CartAdapter
import pl.nw.zadanie_06.common.local_db.LocalDatabase
import pl.nw.zadanie_06.databinding.CartBinding
import pl.nw.zadanie_06.models.view.CartViewModel
import pl.nw.zadanie_06.utils.CartUtils
import pl.nw.zadanie_06.utils.UserUtils

class CartFragment : Fragment(R.layout.cart) {

    private var _binding: CartBinding? = null
    private val binding get() = _binding!!
    private val user = FirebaseAuth.getInstance().currentUser
    private val viewModel: CartViewModel by viewModels {
        val db = LocalDatabase.getInstance(requireActivity().applicationContext)
        UserUtils.ensureAuth(requireActivity())
        CartViewModel.Factory(db, user!!.uid)
    }
    private lateinit var adapter: CartAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CartBinding.inflate(layoutInflater)
        val db = LocalDatabase.getInstance(requireActivity().applicationContext)
        UserUtils.ensureAuth(requireActivity())
        this.adapter = CartAdapter(db, user!!.uid, CartUtils::removeFromCart, CartUtils::addToCart)
        binding.cartitemList.layoutManager = LinearLayoutManager(requireContext())
        binding.cartitemList.adapter = this.adapter
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleScope.launch {
                viewModel.uiState.collect { value ->
                    adapter.setData(value.products, value.items)
                }
            }
        }

        binding.checkoutButton.setOnClickListener {
            val intent = Intent(activity, CheckoutActivity::class.java)
            startActivity(intent)
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
