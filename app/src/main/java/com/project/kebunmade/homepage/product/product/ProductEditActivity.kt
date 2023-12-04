package com.project.kebunmade.homepage.product.product

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.project.kebunmade.R
import com.project.kebunmade.databinding.ActivityProductEditBinding
import com.project.kebunmade.homepage.HomepageActivity

class ProductEditActivity : AppCompatActivity() {

    private var binding: ActivityProductEditBinding? = null
    private var model: ProductModel? = null
    private var dp: String? = null
    private val REQUEST_FROM_GALLERY = 1001


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductEditBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        model = intent.getParcelableExtra(EXTRA_PRODUCT)
        Glide.with(this)
            .load(model?.image)
            .into(binding!!.image)

        binding?.name?.setText(model?.name)
        binding?.description?.setText(model?.description)
        binding?.info?.setText(model?.info)
        binding?.caraPenyimpanan?.setText(model?.caraPenyimpanan)
        binding?.price?.setText(model?.price.toString())


        binding?.backButton?.setOnClickListener {
            onBackPressed()
        }

        binding?.add?.setOnClickListener {
            updateProduct()
        }


        // KLIK TAMBAH GAMBAR
        binding?.imageHint?.setOnClickListener {
            ImagePicker.with(this)
                .galleryOnly()
                .compress(1024)
                .start(REQUEST_FROM_GALLERY);
        }
    }

    private fun updateProduct() {
        val name = binding?.name?.text.toString().trim()
        val description = binding?.description?.text.toString().trim()
        val info = binding?.info?.text.toString().trim()
        val caraPenyimpanan = binding?.caraPenyimpanan?.text.toString().trim()
        val price = binding?.price?.text.toString().trim()

        when {
            name.isEmpty() -> {
                Toast.makeText(this, "Nama produk tidak boleh kosong", Toast.LENGTH_SHORT).show()
            }
            description.isEmpty() -> {
                Toast.makeText(this, "Deskripsi produk tidak boleh kosong", Toast.LENGTH_SHORT)
                    .show()
            }
            info.isEmpty() -> {
                Toast.makeText(
                    this,
                    "Informasi mengenai produk tidak boleh kosong",
                    Toast.LENGTH_SHORT
                ).show()
            }
            price.isEmpty() || price == "0" -> {
                Toast.makeText(this, "Harga produk tidak boleh kosong atau nol", Toast.LENGTH_SHORT)
                    .show()
            }
            caraPenyimpanan.isEmpty() -> {
                Toast.makeText(
                    this,
                    "Cara penyimpanan produk tidak boleh kosong",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else -> {

                binding?.progressBar?.visibility = View.VISIBLE

                if (dp == null) {
                    val data = mapOf(
                        "info" to info,
                        "caraPenyimpanan" to caraPenyimpanan,
                        "name" to name,
                        "description" to description,
                        "price" to price.toLong()
                    )

                    model?.productId?.let { it ->
                        FirebaseFirestore
                            .getInstance()
                            .collection("product")
                            .document(it)
                            .update(data)
                            .addOnCompleteListener { data ->
                                if (data.isSuccessful) {
                                    binding?.progressBar?.visibility = View.GONE
                                    showSuccessDialog()
                                } else {
                                    binding?.progressBar?.visibility = View.GONE
                                    showFailureDialog()
                                }
                            }
                    }

                } else {
                    val data = mapOf(
                        "info" to info,
                        "caraPenyimpanan" to caraPenyimpanan,
                        "image" to dp,
                        "name" to name,
                        "description" to description,
                        "price" to price.toLong()
                    )

                    model?.productId?.let {
                        FirebaseFirestore
                            .getInstance()
                            .collection("product")
                            .document(it)
                            .update(data)
                            .addOnCompleteListener { data ->
                                if (data.isSuccessful) {
                                    binding?.progressBar?.visibility = View.GONE
                                    showSuccessDialog()
                                } else {
                                    binding?.progressBar?.visibility = View.GONE
                                    showFailureDialog()
                                }
                            }
                    }
                }
            }
        }
    }

    private fun showFailureDialog() {
        AlertDialog.Builder(this)
            .setTitle("Gagal Memperbarui Produk")
            .setMessage("Terdapat kesalahan ketika memperbarui produk, silahkan periksa koneksi internet anda, dan coba lagi nanti")
            .setIcon(R.drawable.ic_baseline_clear_24)
            .setPositiveButton("OKE") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .show()
    }

    private fun showSuccessDialog() {
        AlertDialog.Builder(this)
            .setTitle("Berhasil Memperbarui Produk")
            .setMessage("Sukses")
            .setIcon(R.drawable.ic_baseline_check_circle_outline_24)
            .setPositiveButton("OKE") { dialogInterface, _ ->
                dialogInterface.dismiss()
                val intent = Intent(this, HomepageActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
            .show()
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
        val imageFileName = "product/image_" + System.currentTimeMillis() + ".png"
        mStorageRef.child(imageFileName).putFile(data!!)
            .addOnSuccessListener {
                mStorageRef.child(imageFileName).downloadUrl
                    .addOnSuccessListener { uri: Uri ->
                        mProgressDialog.dismiss()
                        dp = uri.toString()
                        Glide
                            .with(this)
                            .load(dp)
                            .into(binding!!.image)
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

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    companion object {
        const val EXTRA_PRODUCT = "product"
    }
}