package com.example.vivekbhalodiya.chatbox.feature.Chatting

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.TextInputEditText
import android.text.format.DateFormat
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import com.example.vivekbhalodiya.chatbox.R
import com.example.vivekbhalodiya.chatbox.feature.firebase.model.ChatMessages
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import android.widget.TextView
import com.firebase.ui.database.FirebaseListAdapter
import com.firebase.ui.database.FirebaseListOptions
import org.jetbrains.annotations.NotNull
import java.util.Timer

class ChattingActivity : AppCompatActivity() , ChattingView{

    private lateinit var mFirebaseAnalytics: FirebaseAnalytics
    private lateinit var adapter: FirebaseListAdapter<ChatMessages>
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

    private fun initializeFabButton(){
        val fab = findViewById<FloatingActionButton>(R.id.fab)
        val input = findViewById<EditText>(R.id.input_text)
        fab.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                FirebaseDatabase.getInstance()
                        .getReference()
                        .push()
                        .setValue(ChatMessages(input.text.toString(), FirebaseAuth.getInstance().currentUser?.displayName.toString()))
                input.text.clear()
            }

        })
    }
    private fun checkUserStatus() {
        if (FirebaseAuth.getInstance().currentUser == null) {
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(),SIGIN_REQUEST_CODE)
        } else {
            Toast.makeText(this, "Welcome " + FirebaseAuth.getInstance().currentUser?.displayName, Toast.LENGTH_SHORT).show()
            displayChatMessages()
        }
    }
    private fun displayChatMessages(){
        val listOfMessages = findViewById<ListView>(R.id.list_of_messages)
        val query = FirebaseDatabase.getInstance().getReference()
        val options = FirebaseListOptions.Builder<ChatMessages>()
                .setQuery(query,ChatMessages::class.java)
                .setLayout(R.layout.messages)
                .build()

        adapter = object : FirebaseListAdapter<ChatMessages>(options) {
            override fun populateView(v: View, model: ChatMessages, position: Int) {
                // Get references to the views of message.xml
                Log.i("Status","In PopulateView")
                val messageText = v.findViewById<View>(R.id.message_text) as TextView
                val messageUser = v.findViewById<View>(R.id.message_user) as TextView
                val messageTime = v.findViewById<View>(R.id.message_time) as TextView

                // Set their text
                messageText.text=model.messageText
                Log.i("Textview",messageText.text.toString())
                messageUser.text=model.messageUser

                // Format the date before showing it
                messageTime.text=DateFormat.format("dd-MM-yyyy (HH:mm:ss)",
                        model.messageTime)
            }
        }
        listOfMessages.adapter=adapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item?.itemId == R.id.menu_sign_out){
            AuthUI.getInstance().signOut(this)
                    .addOnCompleteListener(object :  OnCompleteListener<Void> {
                        override fun onComplete(p0: Task<Void>) {
                            Toast.makeText(this@ChattingActivity,"You have signed out.",Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    })
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode== SIGIN_REQUEST_CODE){
            if(resultCode== Activity.RESULT_OK){
                Log.i("Status ","Success")
                Toast.makeText(this,"Successfully signed in. Welcome!",Toast.LENGTH_SHORT).show()
                displayChatMessages()
            }
            else {
                Log.i("Status ","Fail")
                Toast.makeText(this, "We couldn't sign you in. Please try again later.", Toast.LENGTH_LONG).show();
                finish()
            }
        }

    }
}
