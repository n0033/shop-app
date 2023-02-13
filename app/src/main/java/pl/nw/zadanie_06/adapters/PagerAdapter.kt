package pl.nw.zadanie_06.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import pl.nw.zadanie_06.fragments.*
import pl.nw.zadanie_06.fragments.my_account.*

class PagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 5

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ProductListFragment()
            1 -> CartFragment()
            2 -> CategoryListFragment()
            3 -> AddProductFragment()
            4 -> MyAccountFragment()
            else -> ProductListFragment()
        }
    }
}
