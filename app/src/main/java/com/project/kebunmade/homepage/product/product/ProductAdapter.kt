package com.project.kebunmade.homepage.product.product

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.kebunmade.databinding.ItemProductGridBinding
import java.text.DecimalFormat
import java.text.NumberFormat

class ProductAdapter : RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    private val productList = ArrayList<ProductModel>()
    @SuppressLint("NotifyDataSetChanged")
    fun setData(items: ArrayList<ProductModel>) {
        productList.clear()
        productList.addAll(items)
        notifyDataSetChanged()
    }


    inner class ViewHolder(private val binding: ItemProductGridBinding)  : RecyclerView.ViewHolder(binding.root){
        @SuppressLint("SetTextI18n")
        fun bind(model: ProductModel) {
            val formatter: NumberFormat = DecimalFormat("#,###")
            with(binding) {
                Glide.with(itemView.context)
                    .load(model.image)
                    .into(image)

                title.text = model.name
                description.text = model.description
                price.text = "Rp. ${formatter.format(model.price)}"


                cv.setOnClickListener {
                    val intent = Intent(itemView.context, ProductDetailActivity::class.java)
                    intent.putExtra(ProductDetailActivity.EXTRA_PRODUCT, model)
                    itemView.context.startActivity(intent)
                }

                appCompatButton.setOnClickListener {
                    val mProgressDialog = ProgressDialog(itemView.context)

                    mProgressDialog.setMessage("Mohon tunggu hingga proses selesai...")
                    mProgressDialog.setCanceledOnTouchOutside(false)
                    mProgressDialog.show()


                    val cartId = System.currentTimeMillis().toString()
                    val uid = FirebaseAuth.getInstance().currentUser!!.uid

                    val cart = mapOf(
                        "cartId" to cartId,
                        "userId" to uid,
                        "name" to model.name,
                        "qty" to 1,
                        "price" to model.price,
                        "description" to model.description,
                        "image" to model.image,
                        "productId" to model.productId,
                        "category" to model.category
                    )

                    FirebaseFirestore
                        .getInstance()
                        .collection("cart")
                        .document(cartId)
                        .set(cart)
                        .addOnCompleteListener {
                            if(it.isSuccessful) {
                                mProgressDialog.dismiss()
                                Toast.makeText(itemView.context, "Berhasil menambahkan produk ${model.name} kedalam keranjang!", Toast.LENGTH_SHORT).show()
                            } else {
                                mProgressDialog.dismiss()
                                Toast.makeText(itemView.context, "Ups,anda gagal menambahkan produk ${model.name} kedalam keranjang!", Toast.LENGTH_SHORT).show()
                            }
                        }

                }

            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemProductGridBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(productList[position])
        holder.setIsRecyclable(false)
    }

    override fun getItemCount(): Int = productList.size
}