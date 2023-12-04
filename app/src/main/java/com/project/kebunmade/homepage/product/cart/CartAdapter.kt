package com.project.kebunmade.homepage.product.cart

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.project.kebunmade.databinding.ItemCartBinding
import java.text.DecimalFormat
import java.text.NumberFormat

class CartAdapter(
    private val subTotalPrice: TextView?,
    private val checkoutButton: Button?,
    private val option: String
) : RecyclerView.Adapter<CartAdapter.ViewHolder>() {

    private val cartList = ArrayList<CartModel>()
    @SuppressLint("NotifyDataSetChanged")
    fun setData(items: ArrayList<CartModel>) {
        cartList.clear()
        cartList.addAll(items)
        notifyDataSetChanged()
    }


    inner class ViewHolder(private val binding: ItemCartBinding)  : RecyclerView.ViewHolder(binding.root){
        @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
        fun bind(model: CartModel) {
            val formatter: NumberFormat = DecimalFormat("#,###")
            with(binding) {
                Glide.with(itemView.context)
                    .load(model.image)
                    .into(image)

                name.text = model.name
                qty.text = "Kuantitas: ${model.qty.toString()}"
                price.text = "Rp. ${formatter.format(model.price)}"

                if(option == "cart") {
                    delete.visibility = View.VISIBLE
                }

                delete.setOnClickListener {
                    model.cartId?.let { it1 ->
                        FirebaseFirestore
                            .getInstance()
                            .collection("cart")
                            .document(it1)
                            .delete()
                            .addOnCompleteListener { task ->
                                if(task.isSuccessful) {
                                    cartList.removeAt(adapterPosition)
                                    notifyDataSetChanged()

                                    if(cartList.size > 0) {
                                        var subtotal = 0L
                                        for(i in cartList.indices) {
                                            subtotal += cartList[i].price!!
                                        }

                                        subTotalPrice?.text = "Rp. ${formatter.format(subtotal)}"
                                    } else {
                                        checkoutButton?.visibility = View.INVISIBLE
                                        subTotalPrice?.text = "Rp.0"
                                    }


                                } else {
                                    Toast.makeText(itemView.context, "Gagal menghapus produk ini, silahkan periksa koneksi internet anda dan coba lagi nanti", Toast.LENGTH_SHORT).show()
                                }
                            }
                    }
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(cartList[position])
    }

    override fun getItemCount(): Int = cartList.size
}