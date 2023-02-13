package pl.nw.zadanie_06.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import pl.nw.zadanie_06.R
import pl.nw.zadanie_06.adapters.PagerAdapter

class PagerFragment : Fragment() {
    private lateinit var pagerAdapter: PagerAdapter
    private lateinit var viewPager: ViewPager2
    private var tabTitles = arrayOf("Shop", "Cart", "Category", "Add product", "My account")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.pager, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        pagerAdapter = PagerAdapter(this)
        viewPager = view.findViewById(R.id.pager)
        viewPager.adapter = pagerAdapter

        val tabLayout: TabLayout = view.findViewById(R.id.pager_tabs)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
    }
}
