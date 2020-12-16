package com.example.chitchatapp

import android.location.Location
import androidx.lifecycle.ViewModel
import java.util.*

const val url: String = "https://www.stepoutnyc.com/chitchat"

class ChitChatViewModel : ViewModel() {
    private var client: String = "jacob.ruth@mymail.champlain.edu"
    private var key: String = "50f9078f-f254-4c0e-a173-e77db72e2671"

    var messages: MutableList<Message> = mutableListOf<Message>()

    fun getRetrieveMessagesString(skip: Int?, limit: Int?): String {
        var response: String = "$url?client=$client&key=$key"

        if (skip != null)
            response += "&skip=${skip!!}"

        if (limit != null)
            response += "&limit=${limit!!}"

        return response
    }

    fun getSendMessageString(message: String, lat: Double?, lon: Double?): String {
        var response: String = "$url?client=$client&key=$key&message=$message"

        if (lat != null && lon != null)
            response += "&lat=${lat!!}&lon=${lon!!}"

        return response
    }

    fun getLikeMessageString(messageID: String): String {
        return "$url/like/${messageID}?client=$client&key=$key"
    }

    fun getDislikeMessageString(messageID: String): String {
        return "$url/dislike/${messageID}?client=$client&key=$key"
    }
}

class Message() {
    var id: String = ""
    var client: String = ""
    var date: Date = Date()
    var ip: String = ""
    var likes: Int = 0
    var dislikes: Int = 0
    var loc: Pair<Double?, Double?> = Pair(null, null)
    var message: String = ""
}