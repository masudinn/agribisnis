package com.project.kebunmade.homepage.product.product

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.kebunmade.databinding.ActivityProductBinding
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.project.kebunmade.R
import com.project.kebunmade.homepage.product.cart.CartActivity
import com.project.kebunmade.homepage.product.category.CategoryEditActivity
import java.util.*

class ProductActivity : AppCompatActivity() {

    private var binding: ActivityProductBinding? = null
    private var role: String? = null
    private var adapter: ProductAdapter? = null
    private var category: String? = null

    override fun onResume() {
        super.onResume()
        initRecyclerView()
        initProduct("")
    }

    private fun initRecyclerView() {
        category = intent.getStringExtra(EXTRA_CATEGORY)
        binding?.rvProduct?.layoutManager = StaggeredGridLayoutManager(
            2,
            StaggeredGridLayoutManager.VERTICAL
        )
        adapter = ProductAdapter()
        binding?.rvProduct?.adapter = adapter
    }

    private fun initProduct(search: String) {
        val viewModel = ViewModelProvider(this)[ProductViewModel::class.java]

        binding?.progressBar?.visibility = View.VISIBLE
        if(category != "Semua Kategori") {
            if(search == "") {
                viewModel.setListProduct(category!!)
            } else {
                viewModel.setListProductByQuery(category!!, search)
            }
        } else {
            if(search == "") {
                viewModel.setListProductAll()
            } else {
                viewModel.setListProductAllByQuery(search)
            }
        }
        viewModel.getProductList().observe(this) { product ->
            if (product.size > 0) {
                binding?.noData?.visibility = View.GONE
                adapter!!.setData(product)
            } else {
                binding?.noData?.visibility = View.VISIBLE
            }
            binding?.progressBar?.visibility = View.GONE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        checkRole()

        val category = intent.getStringExtra(EXTRA_CATEGORY)
        binding?.category?.text =  category


        binding?.search?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if(s.toString().isEmpty()) {
                    initRecyclerView()
                    initProduct("")
                } else {
                    initRecyclerView()
                    initProduct(s.toString().lowercase(Locale.getDefault()))
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        binding?.productAdd?.setOnClickListener {
            val intent = Intent(this, ProductAddActivity::class.java)
            intent.putExtra(ProductAddActivity.EXTRA_CATEGORY, category)
            startActivity(intent)
        }

        binding?.backButton?.setOnClickListener {
            onBackPressed()
        }

        binding?.cart?.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }

        binding?.edit?.setOnClickListener {
            editCategory()
        }

        binding?.delete?.setOnClickListener {
            deleteCategory()
        }
    }

    private fun editCategory() {
        val intent = Intent(this, CategoryEditActivity::class.java)
        intent.putExtra(CategoryEditActivity.EXTRA_CATEGORY, category)
        startActivity(intent)
    }

    private fun deleteCategory() {
        AlertDialog.Builder(this)
            .setTitle("Konfirmasi Menghapus Kategori")
            .setMessage("Apakah anda yakin ingin menghapus kategori $category ?")
            .setIcon(R.drawable.ic_baseline_warning_24)
            .setPositiveButton("YA") { dialogInterface, _ ->
                dialogInterface.dismiss()

                val mProgressDialog = ProgressDialog(this)
                mProgressDialog.setMessage("Mohon tunggu hingga proses selesai...")
                mProgressDialog.setCanceledOnTouchOutside(false)
                mProgressDialog.show()

                FirebaseFirestore
                    .getInstance()
                    .collection("category")
                    .whereEqualTo("category", category)
                    .limit(1)
                    .get()
                    .addOnSuccessListener { documents ->
                        for(document in documents) {
                            val uid = document.data["uid"].toString()

                            FirebaseFirestore
                                .getInstance()
                                .collection("category")
                                .document(uid)
                                .delete()
                                .addOnCompleteListener {
                                    if(it.isSuccessful) {
                                        mProgressDialog.dismiss()
                                        Toast.makeText(this, "Berhasil menghapus kategori ${category}.", Toast.LENGTH_SHORT).show()
                                        onBackPressed()
                                    } else {
                                        mProgressDialog.dismiss()
                                        Toast.makeText(this, "Gagal menghapus kategori ${category}, mohon periksa koneksi internet anda", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        }
                    }

            }
            .setNegativeButton("TIDAK", null)
            .show()
    }

    private fun checkRole() {
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        FirebaseFirestore
            .getInstance()
            .collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener {
                role = "" + it.data?.get("role")
                if(role == "admin" && category != "Semua Kategori") {
                    binding?.productAdd?.visibility = View.VISIBLE
                    binding?.edit?.visibility = View.VISIBLE
                    binding?.delete?.visibility = View.VISIBLE
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    companion object {
        const val EXTRA_CATEGORY = "category"
    }
}