package com.project.kebunmade

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.himanshurawat.hasher.HashType
import com.himanshurawat.hasher.Hasher
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.kebunmade.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private var binding: ActivityRegisterBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        binding?.backButton?.setOnClickListener {
            onBackPressed()
        }

        binding?.register?.setOnClickListener {
            formValidation()
        }
    }

    private fun formValidation() {
        val phone = binding?.numberPhone?.text.toString().trim()
        val email = binding?.email?.text.toString().trim()
        val password = binding?.password?.text.toString().trim()
        val name = binding?.name?.text.toString().trim()

        if(phone.isEmpty()) {
            Toast.makeText(this, "Nomor handphone tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return
        } else if(phone.length < 11 || phone.length > 14) {
            Toast.makeText(this, "Panjang karakter Nomor handphone adalah 11 - 14 karakter", Toast.LENGTH_SHORT).show()
            return
        } else if (name.isEmpty()) {
            Toast.makeText(this, "Nama Lengkap tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return
        } else if (email.isEmpty()) {
            Toast.makeText(this, "Email tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return
        }  else if (!email.contains("@") || !email.contains(".")) {
            Toast.makeText(this, "Format email yang anda masukkan salah", Toast.LENGTH_SHORT).show()
            return
        } else if (password.isEmpty()) {
            Toast.makeText(this, "Password tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return
        }

        /// registrasi pengguna
        val mProgressDialog = ProgressDialog(this)

        mProgressDialog.setMessage("Mohon tunggu hingga proses selesai...")
        mProgressDialog.setCanceledOnTouchOutside(false)
        mProgressDialog.show()

        FirebaseAuth
            .getInstance()
            .createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val uid = FirebaseAuth.getInstance().currentUser!!.uid

                    val data = mapOf(
                        "uid" to uid,
                        "phone" to phone,
                        "password" to Hasher.hash(password, HashType.SHA_512),
                        "email" to email,
                        "name" to name,
                        "role" to "user",
                    )

                    uid.let { it1 ->
                        FirebaseFirestore
                            .getInstance()
                            .collection("users")
                            .document(it1)
                            .set(data)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    mProgressDialog.dismiss()
                                    showSuccessDialog()
                                } else {
                                    mProgressDialog.dismiss()
                                    showFailureDialog()
                                }
                            }
                    }
                } else {
                    mProgressDialog.dismiss()
                    showFailureDialog()
                }
            }
    }

    /// jika sukses register
    private fun showSuccessDialog() {
        AlertDialog.Builder(this)
            .setTitle("Berhasil Daftar")
            .setMessage("Silahkan login")
            .setIcon(R.drawable.ic_baseline_check_circle_outline_24)
            .setPositiveButton("OKE") { dialogInterface, _ ->
                dialogInterface.dismiss()
                onBackPressed()
            }
            .show()
    }

    /// jika gagal register, munculkan alert dialog gagal
    private fun showFailureDialog() {
        AlertDialog.Builder(this)
            .setTitle("Gagal Daftar")
            .setMessage("Terdapat kesalahan ketika login, silahkan periksa koneksi internet anda, dan coba lagi nanti")
            .setIcon(R.drawable.ic_baseline_clear_24)
            .setPositiveButton("OKE") { dialogInterface, _ -> dialogInterface.dismiss() }
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}