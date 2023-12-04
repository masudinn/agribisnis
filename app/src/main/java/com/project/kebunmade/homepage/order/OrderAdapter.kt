package com.project.kebunmade.homepage.order

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.kebunmade.R
import com.project.kebunmade.databinding.ItemCategoryBinding
import com.project.kebunmade.databinding.ItemOrderBinding
import com.project.kebunmade.homepage.product.category.CategoryModel
import com.project.kebunmade.homepage.product.product.ProductActivity
import java.text.DecimalFormat
import java.text.NumberFormat

class OrderAdapter : RecyclerView.Adapter<OrderAdapter.ViewHolder>() {

    private val orderList = ArrayList<OrderModel>()
    @SuppressLint("NotifyDataSetChanged")
    fun setData(items: ArrayList<OrderModel>) {
        orderList.clear()
        orderList.addAll(items)
        notifyDataSetChanged()
    }


    inner class ViewHolder(private val binding: ItemOrderBinding)  : RecyclerView.ViewHolder(binding.root){
        @SuppressLint("SetTextI18n")
        fun bind(model: OrderModel) {
            val formatter: NumberFormat = DecimalFormat("#,###")
            with(binding) {
                Glide.with(itemView.context)
                    .load(model.image)
                    .into(image)

                orderId.text = "Order ID: INV-${model.orderId}"
                username.text = "Pembeli: ${model.userName}"
                nominal.text = "Nominal: Rp.${formatter.format(model.totalPrice)}"
                date.text = "Waktu: ${model.date}"
                status.text = model.status


                when (model.status) {
                    "Belum Bayar" -> {
                        bgStatus.backgroundTintList =
                            ContextCompat.getColorStateList(itemView.context, android.R.color.holo_red_dark)
                    }
                    "Sudah Bayar" -> {
                        bgStatus.backgroundTintList =
                            ContextCompat.getColorStateList(itemView.context, android.R.color.holo_orange_dark)
                    }
                    "Sedang Dikirim" -> {
                        bgStatus.backgroundTintList =
                            ContextCompat.getColorStateList(itemView.context, android.R.color.holo_blue_dark)
                    }
                    "Selesai" -> {
                        bgStatus.backgroundTintList =
                            ContextCompat.getColorStateList(itemView.context, R.color.green)
                    }
                    "Ditolak" -> {
                        bgStatus.backgroundTintList =
                            ContextCompat.getColorStateList(itemView.context, android.R.color.darker_gray)
                    }
                }


                cv.setOnClickListener {
                    val intent = Intent(itemView.context, OrderDetailActivity::class.java)
                    intent.putExtra(OrderDetailActivity.EXTRA_ORDER, model)
                    itemView.context.startActivity(intent)
                }

            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(orderList[position])
    }

    override fun getItemCount(): Int = orderList.size
}