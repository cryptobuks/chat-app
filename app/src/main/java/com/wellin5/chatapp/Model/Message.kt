package com.wellin5.chatapp.Model

class Message constructor(
    val message: String, val userName: String, val channelId: String,
    val userAvatar: String, val userAvatarColor: String, val id: String, val timestamp: String
)