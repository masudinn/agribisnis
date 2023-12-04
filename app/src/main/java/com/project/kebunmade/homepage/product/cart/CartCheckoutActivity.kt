package com.project.kebunmade.homepage.product.cart

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.kebunmade.R
import com.project.kebunmade.databinding.ActivityCartCheckoutBinding
import com.project.kebunmade.homepage.HomepageActivity
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule

class CartCheckoutActivity : AppCompatActivity() {

    private var binding: ActivityCartCheckoutBinding? = null
    private var adapter: CartAdapter? = null
    private val formatter: NumberFormat = DecimalFormat("#,###")
    private var cartList: ArrayList<CartModel> = ArrayList()
    private var name: String? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartCheckoutBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        initCartList()


        binding?.backButton?.setOnClickListener {
            onBackPressed()
        }

        binding?.checkout?.setOnClickListener {
            val address = binding?.address?.text.toString()
            if(address.isEmpty()) {
                Toast.makeText(this, "Alamat anda tidak boleh kosong", Toast.LENGTH_SHORT).show()
            } else {
                checkoutProduct(address)
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun checkoutProduct(address: String) {

        val mProgressDialog = ProgressDialog(this)
        mProgressDialog.setMessage("Mohon tunggu hingga proses selesai...")
        mProgressDialog.setCanceledOnTouchOutside(false)
        mProgressDialog.show()
        cartList.clear()
        var totalPrice = 0L
        val uid = FirebaseAuth.getInstance().currentUser!!.uid

        getUserName(uid)


        FirebaseFirestore
            .getInstance()
            .collection("cart")
            .whereEqualTo("userId", uid)
            .get()
            .addOnSuccessListener { documents ->
                for (i in documents) {

                    val model = CartModel()

                    model.cartId = i.data["cartId"].toString()
                    model.category = i.data["category"].toString()
                    model.image = i.data["image"].toString()
                    model.name = i.data["name"].toString()
                    model.qty = i.data["qty"].toString().toLong()
                    model.price = i.data["price"] as Long
                    model.description = i.data["description"].toString()
                    model.productId  = i.data["productId"].toString()
                    model.userId = i.data["userId"].toString()

                    totalPrice = totalPrice.plus(model.price!!)
                    cartList.add(model)
                }

                Timer().schedule(2000) {
                    val orderId = System.currentTimeMillis().toString()
                    val df = SimpleDateFormat("dd MMM yyyy, hh:mm:ss")
                    val formattedDate: String = df.format(Date())
                    val data = mapOf(
                        "orderId" to orderId,
                        "userId" to uid,
                        "product" to cartList,
                        "image" to cartList[0].image,
                        "date" to formattedDate,
                        "status" to "Belum Bayar",
                        "totalPrice" to totalPrice,
                        "userName" to name,
                        "address" to address,
                        "ongkir" to 0L,
                        "isOngkirActive" to false,
                        "paymentProof" to ""
                    )


                    FirebaseFirestore
                        .getInstance()
                        .collection("order")
                        .document(orderId)
                        .set(data)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                deleteCart(mProgressDialog)
                            } else {
                                mProgressDialog.dismiss()
                                showFailureDialogCart()
                            }
                        }
                }
            }

    }

    private fun initCartList() {
        binding?.rvCart?.layoutManager = LinearLayoutManager(this)
        adapter = CartAdapter(null, null, "checkout")
        binding?.rvCart?.adapter = adapter

        val viewModel = ViewModelProvider(this)[CartViewModel::class.java]

        binding?.progressBar?.visibility = View.VISIBLE
        viewModel.setListCart()
        viewModel.getCartList().observe(this) { cart ->
            if (cart.size > 0) {
                binding?.progressBar?.visibility = View.GONE
                binding?.checkout?.visibility = View.VISIBLE
                adapter!!.setData(cart)

                setSubtotal()
            } else {
                binding?.progressBar?.visibility = View.GONE
            }
        }
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
                binding?.totalPrice?.text = "Rp. ${formatter.format(totalPrice)}"
                binding?.subtotal?.text = "Rp. ${formatter.format(totalPrice)}"
            }
    }

    private fun deleteCart(mProgressDialog: ProgressDialog) {
        for (i in cartList.indices) {
            cartList[i].cartId?.let {
                FirebaseFirestore
                    .getInstance()
                    .collection("cart")
                    .document(it)
                    .delete()
            }
        }
        mProgressDialog.dismiss()
        showSuccessDialogCart()
    }



    private fun getUserName(uid: String) {
        FirebaseFirestore
            .getInstance()
            .collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener {
                name = "" + it.data?.get("name")
            }
    }


    /// tampilkan dialog box ketika gagal membuat orderan
    private fun showFailureDialogCart() {
        AlertDialog.Builder(this)
            .setTitle("Gagal Membuat Order")
            .setMessage("Terdapat kesalahan ketika membuat order, silahkan periksa koneksi internet anda, dan coba lagi nanti")
            .setIcon(R.drawable.ic_baseline_clear_24)
            .setPositiveButton("OKE") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .show()
    }

    /// tampilkan dialog box ketika sukses menambah membuat order
    private fun showSuccessDialogCart() {
        AlertDialog.Builder(this)
            .setTitle("Berhasil Membuat Order")
            .setMessage("Silahkan cek menu pesanan untuk melakukan pembayaran.\n\nTerima kasih")
            .setIcon(R.drawable.ic_baseline_check_circle_outline_24)
            .setPositiveButton("OKE") { dialogInterface, _ ->
                binding?.totalPrice?.text = "Rp.0"
                binding?.subtotal?.text = "Rp.0"
                binding?.checkout?.visibility = View.GONE
                dialogInterface.dismiss()

                val intent = Intent(this, HomepageActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()


            }
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}