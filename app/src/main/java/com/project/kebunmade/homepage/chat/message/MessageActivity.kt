package com.project.kebunmade.homepage.chat.message

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.project.kebunmade.databinding.ActivityMessageBinding
import com.project.kebunmade.homepage.chat.ChatModel
import android.view.View
import android.widget.Toast

import androidx.lifecycle.ViewModelProvider

import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.*


class MessageActivity : AppCompatActivity() {

    private var binding: ActivityMessageBinding? = null
    private var model: ChatModel? = null
    private var adapter: MessageAdapter? = null
    private val userId = FirebaseAuth.getInstance().currentUser!!.uid
    private var dp: String? = null
    private val REQUEST_FROM_GALLERY = 1001
    private var role: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMessageBinding.inflate(layoutInflater)
        setContentView(binding?.root)


        model = intent.getParcelableExtra(EXTRA_MESSAGE)
        role = intent.getStringExtra(ROLE)
        if(role == "admin") {
            binding?.name?.text = model?.customerName
        }


        initRecyclerView()
        initViewModel()

        binding?.backButton?.setOnClickListener {
            onBackPressed()
        }

        binding?.send?.setOnClickListener {
            sendMessage()
        }

        binding?.attach?.setOnClickListener {
            ImagePicker.with(this)
                .galleryOnly()
                .compress(1024)
                .start(REQUEST_FROM_GALLERY)
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
        val imageFileName = "message/image_" + System.currentTimeMillis() + ".png"
        mStorageRef.child(imageFileName).putFile(data!!)
            .addOnSuccessListener {
                mStorageRef.child(imageFileName).downloadUrl
                    .addOnSuccessListener { uri: Uri ->
                        dp = uri.toString()


                        @SuppressLint("SimpleDateFormat")
                        val getDate = SimpleDateFormat("dd MMM yyyy, HH:mm")
                        val format: String = getDate.format(Date())

                        MessageDatabase.sendChat(dp!!, format, userId, false, model?.customerId, role!!)

                        // LOAD CHAT HISTORY
                        initRecyclerView()
                        initViewModel()
                        mProgressDialog.dismiss()

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

    private fun sendMessage() {
        val message = binding?.messageEt?.text.toString().trim()
        if (message.isEmpty()) {
            Toast.makeText(this, "Pesan tidak boleh kosong", Toast.LENGTH_SHORT).show()
        } else {
            @SuppressLint("SimpleDateFormat")
            val getDate = SimpleDateFormat("dd MMM yyyy, HH:mm")
            val format: String = getDate.format(Date())

            MessageDatabase.sendChat(message, format, userId, true, model?.customerId, role!!)
            binding!!.messageEt.text.clear()

            // LOAD CHAT HISTORY
            initRecyclerView()
            initViewModel()
        }
    }

    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.stackFromEnd = true
        binding!!.chatRv.layoutManager = linearLayoutManager
        adapter = MessageAdapter(role!!)
        binding!!.chatRv.adapter = adapter
    }

    private fun initViewModel() {
        val viewModel = ViewModelProvider(this)[MessageViewModel::class.java]

        binding!!.progressBar.visibility = View.VISIBLE
        viewModel.setListMessage(model?.customerId!!)
        viewModel.getMessageList().observe(this) { messageList ->
            if (messageList.size > 0) {
                adapter!!.setData(messageList)
            }
            binding?.progressBar?.visibility = View.GONE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    companion object {
        const val EXTRA_MESSAGE = "message"
        const val ROLE = "role"
    }
}