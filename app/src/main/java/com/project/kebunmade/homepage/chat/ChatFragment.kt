package com.project.kebunmade.homepage.chat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.kebunmade.databinding.FragmentChatBinding


class ChatFragment : Fragment() {

    private var binding: FragmentChatBinding? = null
    private var adapter: ChatAdapter? = null

    override fun onResume() {
        super.onResume()
        checkRole()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding?.root
    }

    private fun checkRole() {
        val myUid = FirebaseAuth.getInstance().currentUser!!.uid

        FirebaseFirestore
            .getInstance()
            .collection("users")
            .document(myUid)
            .get()
            .addOnSuccessListener {
                if(it.data?.get("role") == "admin") {
                    initRecyclerView("admin")
                    initViewModel("admin")
                } else {
                    initRecyclerView("user")
                    initViewModel("user")
                }
            }
    }

    private fun initRecyclerView(role: String) {
        binding?.rvChat?.layoutManager = LinearLayoutManager(activity)
        adapter = ChatAdapter(role)
        binding?.rvChat?.adapter = adapter
    }

    private fun initViewModel(role: String) {
        val viewModel = ViewModelProvider(this)[ChatViewModel::class.java]

        binding?.progressBar?.visibility = View.VISIBLE
        if(role == "admin") {
            viewModel.setListChatByAdminSide()
        } else {
            val uid = FirebaseAuth.getInstance().currentUser!!.uid
            viewModel.setListChatByCustomerSide(uid)
        }
        viewModel.getChatList().observe(viewLifecycleOwner) { chat ->
            if (chat.size > 0) {
                binding?.noData?.visibility = View.GONE
                adapter!!.setData(chat)
            } else {
                binding?.noData?.visibility = View.VISIBLE
            }
            binding?.progressBar?.visibility = View.GONE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

}