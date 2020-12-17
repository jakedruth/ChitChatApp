package com.example.chitchatapp

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson

const val url: String = "https://www.stepoutnyc.com/chitchat"

class ChitChatViewModel : ViewModel() {
    var client: String = "jacob.ruth@mymail.champlain.edu"
    private var key: String = "50f9078f-f254-4c0e-a173-e77db72e2671"

    var messages = emptyArray<Message>()
    var messageCount: Int = 0

    fun getMessages(context: Context, callBack: () -> Unit) {
        val queue = Volley.newRequestQueue(context)
        val url = getRetrieveMessagesString(0, 5)

        Log.d(TAG, url)

        // Request a string response from the provided URL.
        val stringRequest = StringRequest(Request.Method.GET, url,
                { response ->
                    val gson = Gson()
                    var gsonResponse = gson.fromJson(response, ChitChatResponse::class.java)
                    messageCount = 0
                    messages = Array(gsonResponse.count) { i ->
                        gsonResponse.messages[i]
                    }
                    callBack.invoke()
                },
                {
                    //messages[0].message = "Error, sorry: ${it.message}"
                    callBack.invoke()
                })

        // Add the request to the RequestQueue.
        queue.add(stringRequest)
    }

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


class ChitChatResponse() {
    var count: Int = 0
    var data: String = ""
    var messages = emptyArray<Message>()
}

class Message() {
    var id: String = ""
    var client: String = ""
    var date: String = ""
    var dislikes: Int = 0
    var ip: String = ""
    var likes: Int = 0
    var loc: Array<String> = arrayOf("", "")
    var message: String = ""
}