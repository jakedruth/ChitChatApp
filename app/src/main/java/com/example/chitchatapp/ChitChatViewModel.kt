package com.example.chitchatapp

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.android.volley.*
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.json.JSONObject


const val URL: String = "https://www.stepoutnyc.com/chitchat"

class ChitChatViewModel : ViewModel() {

    var client: String = "jacob.ruth@mymail.champlain.edu"
    private var key: String = "50f9078f-f254-4c0e-a173-e77db72e2671"
    private lateinit var requestQueue :RequestQueue

    var messages = emptyArray<Message>()
    var messageCount: Int = 0
    private var messageLimitOnRefresh: Int = 50

    fun init(context: Context) {
        requestQueue = Volley.newRequestQueue(context)
    }

    fun refreshMessages(callBack: () -> Unit) {
        val url = getRetrieveMessagesString(0, messageLimitOnRefresh)
        Log.d(TAG, url)

        // Request a string response from the provided URL.
        val stringRequest = StringRequest(Request.Method.GET, url,
            { response ->
                Log.d(TAG, response.toString())
                val gson = Gson()
                val gsonResponse = gson.fromJson(response, ChitChatResponse::class.java)
                messageCount = gsonResponse.count
                messages = Array(gsonResponse.count) { i ->
                    val m = gsonResponse.messages[i]
                    m.likedByUser = MainActivity.getLikeMessage(m._id)
                    return@Array m
                }
                callBack.invoke()
            },
            {
                Log.d(TAG, it.toString())
                callBack.invoke()
            })

        // Add the request to the RequestQueue.
        requestQueue.add(stringRequest)
        requestQueue.start()
    }

    fun sendMessage(msg: String, lat: String?, lon: String?, callBack: () -> Unit) {
        val url = getSendMessageString(msg, null, null)
        val stringRequest = StringRequest(Request.Method.POST, url,
            { response ->
                Log.d(TAG, response.toString())
                refreshMessages(callBack)
            },
            {
                Log.d(TAG, it.toString())
                callBack.invoke()
            })

        requestQueue.add(stringRequest)
    }

    fun likeMessage(messageID: String, callBack: () -> Unit) {
        Log.d(TAG, "Liking message with ID: $messageID")
        val url = getLikeMessageString(messageID)

        // Request a string response from the provided URL.
        val stringRequest = StringRequest(Request.Method.GET, url,
            { response ->
                Log.d(TAG, response)
                refreshMessages(callBack)
            },
            {
                Log.d(TAG, it.toString())
                callBack.invoke()
            })

        // Add the request to the RequestQueue.
        requestQueue.add(stringRequest)
    }

    fun dislikeMessage(messageID: String, callBack: () -> Unit) {
        Log.d(TAG, "Disliking message with ID: $messageID")
        val url = getDislikeMessageString(messageID)

        // Request a string response from the provided URL.
        val stringRequest = StringRequest(Request.Method.GET, url,
            { response ->
                Log.d(TAG, response)
                refreshMessages(callBack)
            },
            {
                Log.d(TAG, it.toString())
                callBack.invoke()
            })

        // Add the request to the RequestQueue.
        requestQueue.add(stringRequest)
    }

    private fun getRetrieveMessagesString(skip: Int?, limit: Int?): String {
        var response: String = "$URL?client=$client&key=$key"

        if (skip != null)
            response += "&skip=${skip}"

        if (limit != null)
            response += "&limit=${limit}"

        return response
    }

    private fun getSendMessageString(message: String, lat: Double?, lon: Double?): String {
        var response: String = "$URL?client=$client&key=$key&message=$message"

        if (lat != null && lon != null)
            response += "&lat=${lat}&lon=${lon}"

        return response
    }

    private fun getLikeMessageString(messageID: String): String {
        return "$URL/like/${messageID}?client=$client&key=$key"
    }

    private fun getDislikeMessageString(messageID: String): String {
        return "$URL/dislike/${messageID}?client=$client&key=$key"
    }
}

class ChitChatResult {
    var code: String = ""
    var message: String = ""
    var id: String = ""
    var reason: String = ""
}

class ChitChatResponse {
    var count: Int = 0
    var date: String = ""
    var messages = emptyArray<Message>()
}

class Message {
    var _id: String = ""
    var client: String = ""
    var date: String = ""
    var dislikes: Int = 0
    var ip: String = ""
    var likes: Int = 0
    var loc: Array<String?> = arrayOf(null, null)
    var message: String = ""
    var likedByUser: Boolean? = null
}