package com.project.kebunmade

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.kebunmade.databinding.ActivityLoginBinding
import com.project.kebunmade.homepage.HomepageActivity

class LoginActivity : AppCompatActivity() {

    private var binding: ActivityLoginBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        showOnboardingImage()
        autoLogin()

        binding?.button?.setOnClickListener {
            formValidation()
        }

        binding?.register?.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

    }

    private fun autoLogin() {
        if(FirebaseAuth.getInstance().currentUser != null) {
            startActivity(Intent(this, HomepageActivity::class.java))
            finish()
        }
    }

    private fun formValidation() {
        val number = binding?.numberPhone?.text.toString().trim()
        val password = binding?.password?.text.toString().trim()

        if(number.isEmpty()) {
            Toast.makeText(this, "Nomor handphone tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return
        } else if(number.length < 11 || number.length > 14) {
            Toast.makeText(this, "Panjang karakter Nomor handphone adalah 11 - 14 karakter", Toast.LENGTH_SHORT).show()
            return
        } else if (password.isEmpty()) {
            Toast.makeText(this, "Kata sandi tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return
        }

        val mProgressDialog = ProgressDialog(this)
        mProgressDialog.setMessage("Mohon tunggu hingga proses selesai...")
        mProgressDialog.setCanceledOnTouchOutside(false)
        mProgressDialog.show()

        FirebaseFirestore
            .getInstance()
            .collection("users")
            .whereEqualTo("phone", number)
            .limit(1)
            .get()
            .addOnCompleteListener(OnCompleteListener { task ->
                if (task.result.size() == 0) {
                    /// jika tidak terdapat di database dan email serta password, maka tidak bisa login
                    mProgressDialog.dismiss()
                    showFailureDialog()
                    return@OnCompleteListener
                }

                /// jika terdaftar maka ambil email di database, kemudian lakukan autentikasi menggunakan email & password dari user
                for (snapshot in task.result) {
                    val email = "" + snapshot["email"]

                    /// fungsi untuk mengecek, apakah email yang di inputkan ketika login sudah terdaftar di database atau belum
                    FirebaseAuth
                        .getInstance()
                        .signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                /// jika terdapat di database dan email serta password sama, maka masuk ke homepage
                                mProgressDialog.dismiss()
                                startActivity(Intent(this, HomepageActivity::class.java))
                                finish()
                            } else {
                                /// jika tidak terdapat di database dan email serta password, maka tidak bisa login
                                mProgressDialog.dismiss()
                                showFailureDialog()
                            }
                        }
                }
            })
    }


    /// munculkan dialog ketika gagal login
    private fun showFailureDialog() {
        AlertDialog.Builder(this)
            .setTitle("Gagal melakukan login")
            .setMessage("Silahkan login kembali dengan informasi yang benar")
            .setIcon(R.drawable.ic_baseline_clear_24)
            .setPositiveButton("OKE") { dialogInterface, _ -> dialogInterface.dismiss() }
            .show()
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