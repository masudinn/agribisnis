package com.project.kebunmade.homepage.product.category

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
import com.project.kebunmade.databinding.ActivityCategoryEditBinding
import com.project.kebunmade.homepage.HomepageActivity

class CategoryEditActivity : AppCompatActivity() {

    private var binding: ActivityCategoryEditBinding? = null
    private var dp: String? = null
    private val REQUEST_FROM_GALLERY = 1001
    private var categoryId: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryEditBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val category = intent.getStringExtra(EXTRA_CATEGORY)
        binding?.title?.setText(category)

        getImage(category!!)

        binding?.backButton?.setOnClickListener {
            onBackPressed()
        }


        binding?.edit?.setOnClickListener {
            formValidation()
        }

        // KLIK TAMBAH GAMBAR
        binding?.imageHint?.setOnClickListener {
            ImagePicker.with(this)
                .galleryOnly()
                .compress(1024)
                .start(REQUEST_FROM_GALLERY);
        }


    }

    private fun formValidation() {
        val category = binding?.title?.text.toString().trim()

        when {
            category.isEmpty() -> {
                Toast.makeText(this, "Kategori tidak boleh kosong", Toast.LENGTH_SHORT).show()
            }
            else -> {

                binding?.progressBar?.visibility = View.VISIBLE

                if (dp != null) {

                    val data = mapOf(
                        "category" to category,
                        "image" to dp,
                    )

                    FirebaseFirestore
                        .getInstance()
                        .collection("category")
                        .document(categoryId!!)
                        .update(data)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                binding?.progressBar?.visibility = View.GONE
                                showSuccessDialog()
                            } else {
                                binding?.progressBar?.visibility = View.GONE
                                showFailureDialog()
                            }
                        }

                } else {

                    val data = mapOf(
                        "category" to category,
                    )

                    FirebaseFirestore
                        .getInstance()
                        .collection("category")
                        .document(categoryId!!)
                        .update(data)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
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


    private fun showFailureDialog() {
        AlertDialog.Builder(this)
            .setTitle("Gagal Menambahkan Kategori")
            .setMessage("Terdapat kesalahan ketika menambahkan kategori, silahkan periksa koneksi internet anda, dan coba lagi nanti")
            .setIcon(R.drawable.ic_baseline_clear_24)
            .setPositiveButton("OKE") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .show()
    }

    private fun showSuccessDialog() {
        AlertDialog.Builder(this)
            .setTitle("Berhasil Memperbarui Kategori")
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
        val imageFileName = "category/image_" + System.currentTimeMillis() + ".png"
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

    private fun getImage(category: String) {
        FirebaseFirestore
            .getInstance()
            .collection("category")
            .whereEqualTo("category", category)
            .limit(1)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val image = document.data["image"].toString()
                    categoryId = document.data["uid"].toString()
                    Glide.with(this)
                        .load(image)
                        .into(binding!!.image)
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