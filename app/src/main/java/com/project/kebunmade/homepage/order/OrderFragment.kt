package com.project.kebunmade.homepage.order

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayout
import com.project.kebunmade.databinding.FragmentOrderBinding

class OrderFragment : Fragment() {

    private var binding: FragmentOrderBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentOrderBinding.inflate(inflater,container, false)

        binding?.tabs?.addTab(binding?.tabs?.newTab()!!.setText("Proses"))
        binding?.tabs?.addTab(binding?.tabs?.newTab()!!.setText("Sukses"))
        binding?.tabs?.addTab(binding?.tabs?.newTab()!!.setText("Gagal"))

        val adapter = OrderPagerAdapter(activity, childFragmentManager, binding?.tabs!!.tabCount)

        binding?.viewPager?.adapter = adapter
        binding?.viewPager?.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(binding?.tabs))
        binding?.tabs?.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                binding?.viewPager?.currentItem = tab!!.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })

        return binding?.root
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

}