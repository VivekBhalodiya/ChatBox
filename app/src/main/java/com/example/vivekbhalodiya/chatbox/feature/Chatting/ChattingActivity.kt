package com.example.vivekbhalodiya.chatbox.feature.Chatting

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.Toast
import com.example.vivekbhalodiya.chatbox.R
import com.example.vivekbhalodiya.chatbox.feature.firebase.model.ChatMessages
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class ChattingActivity : AppCompatActivity(), ChattingView {

    private lateinit var mFirebaseAnalytics: FirebaseAnalytics
    private lateinit var adapter: FirebaseRecyclerAdapter<ChatMessages, ChattingViewHolder>

    companion object {
        val SIGIN_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)

        checkUserStatus()
        initializeFabButton()
    }

    private fun initializeFabButton() {
        val fab = findViewById<FloatingActionButton>(R.id.fab)
        val input = findViewById<EditText>(R.id.input_text)
        fab.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                FirebaseDatabase.getInstance()
                        .reference
                        .child("chats")
                        .push()
                        .setValue(ChatMessages(input.text.toString(), FirebaseAuth.getInstance().currentUser?.displayName.toString()))
                input.text.clear()
            }

        })
    }

    private fun checkUserStatus() {
        if (FirebaseAuth.getInstance().currentUser == null) {
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(), SIGIN_REQUEST_CODE)
        } else {
            Toast.makeText(this, "Welcome " + FirebaseAuth.getInstance().currentUser?.displayName, Toast.LENGTH_SHORT).show()
            displayChatMessages()
        }
    }

    private fun displayChatMessages() {
        val listOfMessages = findViewById<RecyclerView>(R.id.list_of_messages)
        listOfMessages.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val query = FirebaseDatabase.getInstance().reference.child("chats")
        val options = FirebaseRecyclerOptions.Builder<ChatMessages>().setQuery(query, ChatMessages::class.java).build()

        adapter = object : FirebaseRecyclerAdapter<ChatMessages, ChattingViewHolder>(options) {
            override fun onBindViewHolder(holder: ChattingViewHolder, position: Int, model: ChatMessages) {
                holder.bindChats(model)
            }

            override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ChattingViewHolder {
                val view = LayoutInflater.from(this@ChattingActivity).inflate(R.layout.messages, parent, false)
                return ChattingViewHolder(view)
            }

        }
        listOfMessages.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.menu_sign_out) {
            AuthUI.getInstance().signOut(this)
                    .addOnCompleteListener(object : OnCompleteListener<Void> {
                        override fun onComplete(p0: Task<Void>) {
                            Toast.makeText(this@ChattingActivity, "You have signed out.", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    })
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SIGIN_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Log.i("Status ", "Success")
                Toast.makeText(this, "Successfully signed in. Welcome!", Toast.LENGTH_SHORT).show()
                displayChatMessages()
            } else {
                Log.i("Status ", "Fail")
                Toast.makeText(this, "We couldn't sign you in. Please try again later.", Toast.LENGTH_LONG).show();
                finish()
            }
        }

    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()

    }
}
