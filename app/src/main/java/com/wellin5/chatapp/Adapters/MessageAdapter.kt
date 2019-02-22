package com.wellin5.chatapp.Adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.wellin5.chatapp.Model.Message
import com.wellin5.chatapp.R
import com.wellin5.chatapp.Services.UserDataService

class MessageAdapter (val context: Context, val messages: ArrayList<Message>): RecyclerView.Adapter<MessageAdapter.ViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.message_list_view, p0, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return messages.count();
    }

    override fun onBindViewHolder(holder: ViewHolder, p1: Int) {
        holder?.bindMessage(context, messages[p1])
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userImage = itemView?.findViewById<ImageView>(R.id.imgMessageUserImage)
        val timeStamp = itemView?.findViewById<TextView>(R.id.lblMessageTimestamp)
        val userName = itemView?.findViewById<TextView>(R.id.lblMessageUserName)
        val messageBody = itemView?.findViewById<TextView>(R.id.lblMessageBody)

        fun bindMessage(context: Context, message: Message) {
            val resourceId = context.resources.getIdentifier(message.userAvatar, "drawable", context.packageName)
            userImage?.setImageResource(resourceId)
            userImage?.setBackgroundColor(UserDataService.returnAvatarColor(message.userAvatarColor))
            userName?.text = message.userName
            timeStamp?.text = message.timestamp
            messageBody?.text = message.message
        }
    }
}