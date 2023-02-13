package pl.nw.zadanie_06.fragments.my_account

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import pl.nw.zadanie_06.R
import pl.nw.zadanie_06.activities.OrderListActivity
import pl.nw.zadanie_06.databinding.MyAccountBinding
import pl.nw.zadanie_06.utils.UserUtils

class MyAccountFragment : Fragment(R.layout.my_account) {

    private var _binding: MyAccountBinding? = null
    private val binding get() = _binding!!
    private val user = FirebaseAuth.getInstance().currentUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MyAccountBinding.inflate(layoutInflater)
        UserUtils.ensureAuth(requireActivity())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.gotoOrdersButton.setOnClickListener {
            val intent = Intent(activity, OrderListActivity::class.java)
            startActivity(intent)
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
