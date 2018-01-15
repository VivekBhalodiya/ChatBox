package com.example.vivekbhalodiya.chatbox.feature.Chatting

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.TextView
import com.example.vivekbhalodiya.chatbox.R
import com.example.vivekbhalodiya.chatbox.feature.firebase.model.ChatMessages
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.DateFormat
import java.util.Date

/**
 * Created by vivekbhalodiya on 1/15/18.
 */
class ChattingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val mView = itemView
    val context = itemView.context
    val messageText = itemView.findViewById<View>(R.id.message_text) as TextView
    val messageUser = itemView.findViewById<View>(R.id.message_user) as TextView
    val messageTime = itemView.findViewById<View>(R.id.message_time) as TextView

    fun bindChats(chatMessagesModel: ChatMessages) {
        var chatMessages = chatMessagesModel
        FirebaseDatabase.getInstance().reference.child("chats").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                Log.i("Status", "Failed")
            }

            override fun onDataChange(dataSnapshot: DataSnapshot?) {
                for (value: DataSnapshot in dataSnapshot!!.children) {
                    chatMessages = value.getValue(ChatMessages::class.java)!!
                }
                Log.i("Status",chatMessages.messageText)

            }
        })
        messageText.text = chatMessages.messageText
        messageUser.text = chatMessages.messageUser
        messageTime.text = DateFormat.getDateTimeInstance().format(Date())
    }

}