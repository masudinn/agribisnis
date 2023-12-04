package com.project.kebunmade.homepage.product.cart

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.kebunmade.databinding.ActivityCartBinding
import java.text.DecimalFormat
import java.text.NumberFormat

class CartActivity : AppCompatActivity() {


    private var binding: ActivityCartBinding ? = null
    private var adapter: CartAdapter? = null
    private val formatter: NumberFormat = DecimalFormat("#,###")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        initCartList()


        binding?.backButton?.setOnClickListener {
            onBackPressed()
        }

        binding?.checkout?.setOnClickListener {
            checkoutProduct()
        }
    }

    private fun checkoutProduct() {
        startActivity(Intent(this, CartCheckoutActivity::class.java))
    }




    @SuppressLint("SetTextI18n")
    private fun setSubtotal() {
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        var totalPrice = 0L
        FirebaseFirestore
            .getInstance()
            .collection("cart")
            .whereEqualTo("userId", uid)
            .get()
            .addOnSuccessListener { documents ->
                for (i in documents) {
                    totalPrice = totalPrice.plus(i.data["price"] as Long)
                }
                binding?.subtotal?.text = "Rp. ${formatter.format(totalPrice)}"
            }
    }

    private fun initCartList() {
        val subTotalPrice = binding?.subtotal
        val checkoutButton = binding?.checkout
        binding?.rvCart?.layoutManager = LinearLayoutManager(this)
        adapter = CartAdapter(subTotalPrice, checkoutButton, "cart")
        binding?.rvCart?.adapter = adapter

        val viewModel = ViewModelProvider(this)[CartViewModel::class.java]

        binding?.progressBar?.visibility = View.VISIBLE
        viewModel.setListCart()
        viewModel.getCartList().observe(this) { cart ->
            if (cart.size > 0) {
                binding?.progressBar?.visibility = View.GONE
                binding?.noData?.visibility = View.GONE
                binding?.checkout?.visibility = View.VISIBLE
                adapter!!.setData(cart)

                setSubtotal()
            } else {
                binding?.progressBar?.visibility = View.GONE
                binding?.noData?.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}