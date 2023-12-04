package com.project.kebunmade.homepage.order.status

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.kebunmade.databinding.FragmentOrderSuccessBinding
import com.project.kebunmade.homepage.order.OrderAdapter
import com.project.kebunmade.homepage.order.OrderViewModel


class OrderSuccessFragment : Fragment() {

    private var binding: FragmentOrderSuccessBinding? = null
    private var adapter: OrderAdapter? = null
    private val uid = FirebaseAuth.getInstance().currentUser!!.uid


    override fun onResume() {
        super.onResume()
        checkRole()
    }

    private fun checkRole() {
        FirebaseFirestore
            .getInstance()
            .collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener {
                if(it.data?.get("role") == "user") {
                    initProcessOrder("user")
                } else {
                    initProcessOrder("admin")
                }
            }
    }

    private fun initProcessOrder(role: String) {
        val viewModel = ViewModelProvider(this)[OrderViewModel::class.java]
        binding?.progressBar?.visibility = View.VISIBLE

        binding?.rvOrderSuccess?.layoutManager = LinearLayoutManager(activity)
        adapter = OrderAdapter()
        binding?.rvOrderSuccess?.adapter = adapter

        if(role == "user") {
            viewModel.setListOrderByIDSuceess(uid)
        } else {
            viewModel.setListOrderSuccessByAll()
        }
        viewModel.getOrderList().observe(viewLifecycleOwner) { success ->
            if (success.size > 0) {
                binding?.progressBar?.visibility = View.GONE
                binding?.noData?.visibility = View.GONE
                adapter!!.setData(success)
            } else {
                binding?.progressBar?.visibility = View.GONE
                binding?.noData?.visibility = View.VISIBLE
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentOrderSuccessBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }


}