package com.project.kebunmade.homepage.chat

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.kebunmade.databinding.ItemChatBinding
import com.project.kebunmade.homepage.chat.message.MessageActivity

class ChatAdapter(private val role: String) : RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    private val chatList = ArrayList<ChatModel>()
    @SuppressLint("NotifyDataSetChanged")
    fun setData(items: ArrayList<ChatModel>) {
        chatList.clear()
        chatList.addAll(items)
        notifyDataSetChanged()
    }


    inner class ViewHolder(private val binding: ItemChatBinding)  : RecyclerView.ViewHolder(binding.root){
        @SuppressLint("SetTextI18n")
        fun bind(model: ChatModel) {
            with(binding) {

                if(role == "admin") {
                    username.text = model.customerName
                    date.text = model.dateTime
                    lastMessage.text = "Pesan Terakhir: ${model.lastMessage}"

                    cv.setOnClickListener {
                        val intent = Intent(itemView.context, MessageActivity::class.java)
                        intent.putExtra(MessageActivity.EXTRA_MESSAGE, model)
                        intent.putExtra(MessageActivity.ROLE, "admin")
                        itemView.context.startActivity(intent)
                    }
                } else {
                    username.text = "Admin Kebun Made"
                    date.text = model.dateTime
                    lastMessage.text = "Pesan Terakhir: ${model.lastMessage}"

                    cv.setOnClickListener {
                        val intent = Intent(itemView.context, MessageActivity::class.java)
                        intent.putExtra(MessageActivity.EXTRA_MESSAGE, model)
                        intent.putExtra(MessageActivity.ROLE, "user")
                        itemView.context.startActivity(intent)
                    }
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemChatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(chatList[position])
    }

    override fun getItemCount(): Int = chatList.size
}