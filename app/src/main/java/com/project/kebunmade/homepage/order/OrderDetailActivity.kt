package com.project.kebunmade.homepage.order

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.project.kebunmade.R
import com.project.kebunmade.databinding.ActivityOrderDetailBinding
import com.project.kebunmade.homepage.product.cart.CartAdapter
import com.project.kebunmade.homepage.product.cart.CartModel
import java.text.DecimalFormat
import java.text.NumberFormat

class OrderDetailActivity : AppCompatActivity() {

    private var binding: ActivityOrderDetailBinding? = null
    private var model: OrderModel? = null
    private var ongkir: Long? = 0L
    private var dp: String? = null
    private val REQUEST_FROM_GALLERY = 1001
    private var status: String? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderDetailBinding.inflate(layoutInflater)
        setContentView(binding?.root)


        val formatter: NumberFormat = DecimalFormat("#,###")
        val myUid = FirebaseAuth.getInstance().currentUser!!.uid
        model = intent.getParcelableExtra(EXTRA_ORDER)
        initOrderProduct()

        ongkir = model?.ongkir
        status = model?.status

        checkRole()

        if(model?.paymentProof != "") {
            binding?.imageHint?.visibility = View.GONE
            Glide.with(this)
                .load(model?.paymentProof)
                .into(binding!!.image)
        }

        if(myUid == model?.userId && status == "Belum Bayar") {
            binding?.imageHint?.visibility = View.VISIBLE
        }

        binding?.orderId?.text = "INV-${model?.orderId}"
        binding?.username?.text = model?.userName
        binding?.date?.text = model?.date
        binding?.address?.text = model?.address
        binding?.status?.text = status
        binding?.totalPrice?.text = "Rp.${
           formatter.format(model?.totalPrice?.plus(ongkir!!))
        }"
        binding?.ongkir?.text = "Rp.${formatter.format(model?.ongkir)}"
        binding?.nominal?.text = "Rp.${formatter.format(model?.totalPrice)}"

        when (status) {
            "Belum Bayar" -> {
                binding?.view19?.backgroundTintList =
                    ContextCompat.getColorStateList(this, android.R.color.holo_red_dark)
            }
            "Sudah Bayar" -> {
                binding?.view19?.backgroundTintList =
                    ContextCompat.getColorStateList(this, android.R.color.holo_orange_dark)
            }
            "Sedang Dikirim" -> {
                binding?.view19?.backgroundTintList =
                    ContextCompat.getColorStateList(this, android.R.color.holo_blue_dark)
            }
            "Selesai" -> {
                binding?.view19?.backgroundTintList =
                    ContextCompat.getColorStateList(this, R.color.green)
            }
            "Ditolak" -> {
                binding?.view19?.backgroundTintList =
                    ContextCompat.getColorStateList(this, android.R.color.darker_gray)
            }
        }

        binding?.backButton?.setOnClickListener {
            onBackPressed()
        }

        binding?.setOngkir?.setOnClickListener {
            setOngkir()
        }

        // KLIK TAMBAH GAMBAR
        binding?.imageHint?.setOnClickListener {
            if(model?.isOngkirActive == true) {
                ImagePicker.with(this)
                    .galleryOnly()
                    .compress(1024)
                    .start(REQUEST_FROM_GALLERY)
            } else {
                Toast.makeText(this, "Silahkan tunggu hingga Admin menerapkan ongkos kirim untuk orderan anda.", Toast.LENGTH_SHORT).show()
            }
        }


        binding?.decline?.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Konfirmasi Menolak Orderan")
                .setMessage("Apakah anda yakin ingin menolak orderan ini ?")
                .setIcon(R.drawable.ic_baseline_warning_24)
                .setPositiveButton("YA") { dialogInterface, _ ->
                    dialogInterface.dismiss()
                    model?.orderId?.let { it1 ->
                        FirebaseFirestore
                            .getInstance()
                            .collection("order")
                            .document(it1)
                            .update("status", "Ditolak")
                            .addOnCompleteListener { task ->
                                if(task.isSuccessful) {
                                    onBackPressed()
                                    Toast.makeText(this, "Berhasil menolak orderan ini", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(this, "Gagal menolak orderan ini, mungkin terdapat kendala di koneksi internet anda", Toast.LENGTH_SHORT).show()
                                }
                            }
                    }
                }
                .setNegativeButton("TIDAK", null)
                .show()
        }


        binding?.acc?.setOnClickListener {
            when (status) {
                "Belum Bayar" -> {
                    if(model?.paymentProof != "" && model?.isOngkirActive == true) {
                        showAccConfirmationDialog("Konfirmasi Pembayaran", "Apakah anda yakin bukti pembayaran yang di upload kustomer sudah sesuai dengan dana yang masuk ke rekening admin\n\nIngin menerima bukti pembayaran ?")
                    } else {
                        Toast.makeText(this, "Anda harus menerapkan Ongkir terlebih dahulu & Kustomer harus mengunggah bukti pembayaran terlebih dahulu", Toast.LENGTH_SHORT).show()
                    }
                }
                "Sudah Bayar" -> {
                    showAccConfirmationDialog("Konfirmasi Pengiriman Barang", "Apakah anda yakin orderan dari kustomer sudah di kirimkan ke alamat tujuan ?")
                }
                "Sedang Dikirim" -> {
                    showAccConfirmationDialog("Konfirmasi Selesai", "Apakah anda yakin orderan sudah sampai di alamat tujuan ?")
                }
            }
        }
    }

    private fun showAccConfirmationDialog(title: String, description: String) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(description)
            .setIcon(R.drawable.ic_baseline_warning_24)
            .setPositiveButton("YA") { dialogInterface, _ ->
                if(status == "Belum Bayar") {
                    model?.orderId?.let { it1 ->
                        FirebaseFirestore
                            .getInstance()
                            .collection("order")
                            .document(it1)
                            .update("status", "Sudah Bayar")
                            .addOnCompleteListener { task ->
                                if(task.isSuccessful) {
                                    onBackPressed()
                                    Toast.makeText(this, "Berhasil melakukan konfirmasi", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(this, "Gagal melakukan konfirmasi, mungkin terdapat kendala di koneksi internet anda", Toast.LENGTH_SHORT).show()
                                }
                                dialogInterface.dismiss()
                            }
                    }
                } else if (status == "Sudah Bayar") {
                    model?.orderId?.let { it1 ->
                        FirebaseFirestore
                            .getInstance()
                            .collection("order")
                            .document(it1)
                            .update("status", "Sedang Dikirim")
                            .addOnCompleteListener { task ->
                                if(task.isSuccessful) {
                                    onBackPressed()
                                    Toast.makeText(this, "Berhasil melakukan konfirmasi", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(this, "Gagal melakukan konfirmasi, mungkin terdapat kendala di koneksi internet anda", Toast.LENGTH_SHORT).show()
                                }
                                dialogInterface.dismiss()
                            }
                    }
                } else if(status == "Sedang Dikirim") {
                    model?.orderId?.let { it1 ->
                        FirebaseFirestore
                            .getInstance()
                            .collection("order")
                            .document(it1)
                            .update("status", "Selesai")
                            .addOnCompleteListener { task ->
                                if(task.isSuccessful) {
                                    onBackPressed()
                                    Toast.makeText(this, "Berhasil melakukan konfirmasi", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(this, "Gagal melakukan konfirmasi, mungkin terdapat kendala di koneksi internet anda", Toast.LENGTH_SHORT).show()
                                }
                                dialogInterface.dismiss()
                            }
                    }
                }
            }
            .setNegativeButton("TIDAK", null)
            .show()
    }

    @SuppressLint("SetTextI18n")
    private fun setOngkir() {
        val btnSubmit: Button
        val btnCancel: Button
        val etOngkir: EditText
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.popup_set_ongkir)
        dialog.setCanceledOnTouchOutside(false)
        btnSubmit = dialog.findViewById(R.id.submit)
        btnCancel = dialog.findViewById(R.id.cancel_button)
        etOngkir = dialog.findViewById(R.id.ongkir)

        btnSubmit.setOnClickListener{

            val value = etOngkir.text.toString().trim()

            /// validasi referral oleh sistem
            if (value.isEmpty()) {
                Toast.makeText(
                    this,
                    "Ongkos Kirim tidak boleh kosong",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val mProgressDialog = ProgressDialog(this)

            mProgressDialog.setMessage("Mohon tunggu hingga proses selesai...")
            mProgressDialog.setCanceledOnTouchOutside(false)
            mProgressDialog.show()
            val formatter: NumberFormat = DecimalFormat("#,###")

            val data = mapOf(
                "ongkir" to value.toLong(),
                "isOngkirActive" to true,
            )

            model?.orderId?.let { it1 ->
                FirebaseFirestore
                    .getInstance()
                    .collection("order")
                    .document(it1)
                    .update(data)
                    .addOnCompleteListener {
                        if(it.isSuccessful) {
                            ongkir = value.toLong()
                            binding?.ongkir?.text = "Rp.${formatter.format(ongkir)}"
                            binding?.totalPrice?.text = "Rp.${formatter.format(ongkir!! + model?.totalPrice!!)}"
                            mProgressDialog.dismiss()
                            dialog.dismiss()
                            Toast.makeText(this, "Berhasil menerapkan ongkos kirim", Toast.LENGTH_SHORT).show()
                        } else {
                            mProgressDialog.dismiss()
                            dialog.dismiss()
                            Toast.makeText(this, "Ups,anda gagal menerapkan ongkos kirim", Toast.LENGTH_SHORT).show()
                        }
                    }
            }


        }
        btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    private fun initOrderProduct() {
        binding?.rvOrder?.layoutManager = LinearLayoutManager(this)
        val adapter = CartAdapter(null, null, "order")
        binding?.rvOrder?.adapter = adapter
        adapter.setData(model?.product as ArrayList<CartModel>)
    }

    private fun checkRole() {
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        FirebaseFirestore
            .getInstance()
            .collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener {
                if (it.data?.get("role") == "admin") {
                    if(status == "Belum Bayar") {
                        binding?.acc?.visibility = View.VISIBLE
                        binding?.decline?.visibility = View.VISIBLE
                        binding?.setOngkir?.visibility = View.VISIBLE
                    } else if (status == "Sudah Bayar" || status == "Sedang Dikirim") {
                        binding?.acc?.visibility = View.VISIBLE
                    }
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_FROM_GALLERY) {
                uploadArticleDp(data?.data)
            }
        }
    }


    /// fungsi untuk mengupload foto kedalam cloud storage
    private fun uploadArticleDp(data: Uri?) {
        val mStorageRef = FirebaseStorage.getInstance().reference
        val mProgressDialog = ProgressDialog(this)
        mProgressDialog.setMessage("Mohon tunggu hingga proses selesai...")
        mProgressDialog.setCanceledOnTouchOutside(false)
        mProgressDialog.show()
        val imageFileName = "paymentProof/image_" + System.currentTimeMillis() + ".png"
        mStorageRef.child(imageFileName).putFile(data!!)
            .addOnSuccessListener {
                mStorageRef.child(imageFileName).downloadUrl
                    .addOnSuccessListener { uri: Uri ->
                        mProgressDialog.dismiss()
                        dp = uri.toString()
                        updatePaymentProof()
                        Glide
                            .with(this)
                            .load(dp)
                            .into(binding!!.image)
                        showSuccessUploadPaymentProof()
                    }
                    .addOnFailureListener { e: Exception ->
                        mProgressDialog.dismiss()
                        Toast.makeText(
                            this,
                            "Gagal mengunggah gambar",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.d("imageDp: ", e.toString())
                    }
            }
            .addOnFailureListener { e: Exception ->
                mProgressDialog.dismiss()
                Toast.makeText(
                    this,
                    "Gagal mengunggah gambar",
                    Toast.LENGTH_SHORT
                )
                    .show()
                Log.d("imageDp: ", e.toString())
            }
    }

    private fun showSuccessUploadPaymentProof() {
        AlertDialog.Builder(this)
            .setTitle("Berhasil Mengunggah Bukti Pembayaran")
            .setMessage("Silahkan tunggu admin Kebun Made memverifikasi bukti pembayaran anda\n\nTerima kasih.")
            .setIcon(R.drawable.ic_baseline_check_circle_outline_24)
            .setPositiveButton("OKE") { dialogInterface, _ ->
                dialogInterface.dismiss()
                onBackPressed()
            }
            .show()
    }

    private fun updatePaymentProof() {
        model?.orderId?.let {
            FirebaseFirestore
                .getInstance()
                .collection("order")
                .document(it)
                .update("paymentProof", dp)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    companion object {
        const val EXTRA_ORDER = "order"
    }
}