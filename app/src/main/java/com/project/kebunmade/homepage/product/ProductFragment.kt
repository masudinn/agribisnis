package com.project.kebunmade.homepage.product

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.kebunmade.LoginActivity
import com.project.kebunmade.R
import com.project.kebunmade.databinding.FragmentProductBinding
import com.project.kebunmade.homepage.product.cart.CartActivity
import com.project.kebunmade.homepage.product.category.CategoryAdapter
import com.project.kebunmade.homepage.product.category.CategoryAddActivity
import com.project.kebunmade.homepage.product.category.CategoryViewModel
import com.project.kebunmade.homepage.product.product.ProductActivity
import com.project.kebunmade.homepage.product.product.ProductFragmentAdapter
import com.project.kebunmade.homepage.product.product.ProductViewModel


class ProductFragment : Fragment() {

    private var binding: FragmentProductBinding? = null
    private var role: String? = null
    private var categoryAdapter: CategoryAdapter? = null
    private var productAdapter: ProductFragmentAdapter? = null

    override fun onResume() {
        super.onResume()
        showCategory()
        initLatestProduct()
    }

    private fun initLatestProduct() {
        val layoutManager = LinearLayoutManager(
            activity,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        binding?.newProductRv?.layoutManager = layoutManager

        productAdapter = ProductFragmentAdapter()
        binding?.newProductRv?.adapter = productAdapter

        val viewModel = ViewModelProvider(this)[ProductViewModel::class.java]

        binding?.progressBarNewProduct?.visibility = View.VISIBLE
        viewModel.setListProductAll()
        viewModel.getProductList().observe(viewLifecycleOwner) { product ->
            if (product.size > 0) {
                binding?.progressBarNewProduct?.visibility = View.GONE
                productAdapter!!.setData(product)
                binding?.noDataNewProduct?.visibility = View.GONE
            } else {
                binding?.progressBarNewProduct?.visibility = View.GONE
                binding?.noDataNewProduct?.visibility = View.VISIBLE
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProductBinding.inflate(inflater, container, false)

        checkRole()
        showOnboardingImage()

        return binding?.root
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
                if(role == "admin") {
                    binding?.categoryAdd?.visibility = View.VISIBLE
                }
            }
    }

    private fun showCategory() {
        binding?.categoryRv?.layoutManager = LinearLayoutManager(
            activity,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        categoryAdapter = CategoryAdapter()
        binding?.categoryRv?.adapter = categoryAdapter

        val viewModel = ViewModelProvider(this)[CategoryViewModel::class.java]

        binding?.categoryPb?.visibility = View.VISIBLE
        viewModel.setListCategory()
        viewModel.getCategoryList().observe(viewLifecycleOwner) { category ->
            if (category.size > 0) {
                categoryAdapter!!.setData(category)
            }
            binding?.categoryPb?.visibility = View.GONE
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.logout?.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            val intent = Intent(activity, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            activity?.finish()
        }

        binding?.cart?.setOnClickListener {
            startActivity(Intent(activity, CartActivity::class.java))
        }

        binding?.seeAllNewProduct?.setOnClickListener {
            val intent = Intent(activity, ProductActivity::class.java)
            intent.putExtra(ProductActivity.EXTRA_CATEGORY, "Semua Kategori")
            startActivity(intent)
        }

        binding?.categoryAdd?.setOnClickListener {
            startActivity(Intent(activity, CategoryAddActivity::class.java))
        }

    }


    /// onboarding merupakan gambar gambar yang otomatis slide pada halaman utama
    private fun showOnboardingImage() {
        val imageList: ArrayList<SlideModel> = ArrayList() // Create image list
        imageList.add(SlideModel(R.drawable.onb1, ScaleTypes.CENTER_CROP))
        imageList.add(SlideModel(R.drawable.onb2, ScaleTypes.CENTER_CROP))
        imageList.add(SlideModel(R.drawable.onb3, ScaleTypes.CENTER_CROP))
        imageList.add(SlideModel(R.drawable.onb4, ScaleTypes.CENTER_CROP))
        imageList.add(SlideModel(R.drawable.onb5, ScaleTypes.CENTER_CROP))
        binding!!.imageView2.setImageList(imageList)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

}