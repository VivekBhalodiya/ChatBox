package com.example.vivekbhalodiya.chatbox.feature.firebase.model

import java.util.Date

/**
 * Created by vivekbhalodiya on 1/10/18.
 */
class ChatMessages(msgText :String,msgUser :String){
    var messageText = msgText
    var messageUser = msgUser
    var messageTime = Date().time

    constructor() : this("Default","Default")
}