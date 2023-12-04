package com.project.kebunmade.homepage.product.category

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.kebunmade.databinding.ItemCategoryBinding
import com.project.kebunmade.homepage.product.product.ProductActivity

class CategoryAdapter : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    private val categoryList = ArrayList<CategoryModel>()
    @SuppressLint("NotifyDataSetChanged")
    fun setData(items: ArrayList<CategoryModel>) {
        categoryList.clear()
        categoryList.addAll(items)
        notifyDataSetChanged()
    }


    inner class ViewHolder(private val binding: ItemCategoryBinding)  :RecyclerView.ViewHolder(binding.root){
        fun bind(model: CategoryModel) {
            with(binding) {
                Glide.with(itemView.context)
                    .load(model.image)
                    .into(image)

                title.text = model.category
                cv.setOnClickListener {
                    val intent = Intent(itemView.context, ProductActivity::class.java)
                    intent.putExtra(ProductActivity.EXTRA_CATEGORY, model.category)
                    itemView.context.startActivity(intent)
                }

            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(categoryList[position])
    }

    override fun getItemCount(): Int = categoryList.size
}