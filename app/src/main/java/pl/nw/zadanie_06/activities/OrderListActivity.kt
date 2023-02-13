package pl.nw.zadanie_06.activities

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import pl.nw.zadanie_06.adapters.OrderListAdapter
import pl.nw.zadanie_06.common.local_db.LocalDatabase
import pl.nw.zadanie_06.databinding.ActivityOrderListBinding
import pl.nw.zadanie_06.models.view.OrderListViewModel
import pl.nw.zadanie_06.utils.UserUtils

class OrderListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderListBinding
    private lateinit var db: LocalDatabase
    private lateinit var auth: FirebaseAuth
    private val user = FirebaseAuth.getInstance().currentUser
    private val viewModel: OrderListViewModel by viewModels {
        val db = LocalDatabase.getInstance(applicationContext)
        UserUtils.ensureAuth(this)
        OrderListViewModel.Factory(db, user!!.uid)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = LocalDatabase.getInstance(applicationContext)
        UserUtils.ensureAuth(this)
        binding = ActivityOrderListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val adapter = OrderListAdapter()
        binding.orderList.layoutManager =
            LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
        binding.orderList.adapter = adapter

        binding.apply {
            lifecycleScope.launch {
                viewModel.uiState.collect { value ->
                    adapter.setData(value.payments, value.products)
                }
            }
        }
    }


}