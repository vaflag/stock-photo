package com.vf.photobank.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.iammert.library.AnimatedTabLayout
import com.vf.photobank.R
import com.vf.photobank.ui.home.HomeFragment
import com.vf.photobank.ui.search.SearchFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager
    private lateinit var tabLayout: AnimatedTabLayout
    private val fragments = listOf(
        HomeFragment(),
        SearchFragment()
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewPager = view_pager
        tabLayout = tab_layout
        tabLayout.setupViewPager(viewPager)
        viewPager.adapter = ViewPagerAdapter(supportFragmentManager)
    }

    inner class ViewPagerAdapter(fragmentManager: FragmentManager) :
        FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getItem(position: Int) = fragments.getOrNull(position) ?: HomeFragment()
        override fun getCount() = fragments.size
    }
}
