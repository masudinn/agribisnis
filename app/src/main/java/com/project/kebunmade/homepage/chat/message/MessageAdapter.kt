package com.project.kebunmade.homepage.chat.message

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.kebunmade.databinding.ItemChatLeftBinding
import com.project.kebunmade.databinding.ItemChatRightBinding

class MessageAdapter(private val role: String) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val listMessage = ArrayList<MessageModel>()
    @SuppressLint("NotifyDataSetChanged")
    fun setData(items: ArrayList<MessageModel>) {
        listMessage.clear()
        listMessage.addAll(items)
        notifyDataSetChanged()
    }


    companion object {
        private const val MSG_TYPE_LEFT = 0
        private const val MSG_TYPE_RIGHT = 1
        private val TAG = MessageAdapter::class.java.simpleName
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if(viewType == MSG_TYPE_RIGHT) {
            val binding = ItemChatRightBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            MessageViewHolderRight(binding)
        } else {
            val binding = ItemChatLeftBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            MessageViewHolderLeft(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if(listMessage[position].role.equals(role)) {
            (holder as MessageViewHolderRight).bind(listMessage[position])
        } else{
            (holder as MessageViewHolderLeft).bind(listMessage[position])
        }

    }

    override fun getItemCount(): Int = listMessage.size

    override fun getItemViewType(position: Int) : Int{
        //get currently signed user

        return if(listMessage[position].role.equals(role)){
            MSG_TYPE_RIGHT
        } else {
            MSG_TYPE_LEFT
        }
    }

    inner class MessageViewHolderRight(private val binding: ItemChatRightBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(list: MessageModel) {
            if(list.isText == true) {
                binding.imageText.visibility = View.GONE
                binding.messageTv.visibility = View.VISIBLE
                binding.messageTv.text = list.message
            } else {
                binding.messageTv.visibility = View.GONE
                binding.imageText.visibility = View.VISIBLE
                Glide.with(itemView.context)
                    .load(list.message)
                    .into(binding.imageText)
            }
            binding.timeTv.text = list.dateTime
        }
    }

    inner class MessageViewHolderLeft(private val binding: ItemChatLeftBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(list: MessageModel) {
            if(list.isText == true) {
                binding.imageText.visibility = View.GONE
                binding.messageTv.visibility = View.VISIBLE
                binding.messageTv.text = list.message
            } else {
                binding.messageTv.visibility = View.GONE
                binding.imageText.visibility = View.VISIBLE
                Glide.with(itemView.context)
                    .load(list.message)
                    .into(binding.imageText)
            }
            binding.timeTv.text = list.dateTime
        }
    }
}