package com.project.kebunmade.homepage.order

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.project.kebunmade.homepage.order.status.OrderFailureFragment
import com.project.kebunmade.homepage.order.status.OrderProcessFragment
import com.project.kebunmade.homepage.order.status.OrderSuccessFragment

class OrderPagerAdapter(var context: FragmentActivity?, fm: FragmentManager, var totalTabs: Int) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                OrderProcessFragment()
            }
            1 -> {
                OrderSuccessFragment()
            }
            2 -> {
                OrderFailureFragment()
            }
            else -> getItem(position)
        }
    }
    override fun getCount(): Int {
        return totalTabs
    }
}