package com.example.chitchatapp

import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.edit
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*

const val TAG = "MAIN_ACTIVITY"

class MainActivity : AppCompatActivity(), SendMessageDialog.Callbacks {

    companion object {
        private var sharedPreferences: SharedPreferences? = null

        fun tryLikeMessage(id: String, value: Boolean): Boolean {
            if (sharedPreferences == null)
                return false

            val containsID = sharedPreferences!!.contains(id)

            if (containsID) // If the key exist, we can't edit it
                return false

            sharedPreferences?.edit {
                putBoolean(id, value)
                apply()
            }

            return true
        }

        fun getLikeMessage(id: String): Boolean? {
            if (sharedPreferences == null)
                return null

            if (sharedPreferences!!.contains(id))
                return sharedPreferences!!.getBoolean(id, false)

            return null
        }
    }

    private val chitChatViewModel: ChitChatViewModel by lazy {
        return@lazy ChitChatViewModel()
    }

    private lateinit var refreshButton: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var writeMessageButton: ImageButton

    private var adapter = MessagesAdapter(emptyArray())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG, "On Create")

        sharedPreferences = getPreferences(Context.MODE_PRIVATE) ?: return

        setUpView()
        setUpListeners()
        setUpRecyclerView()

        chitChatViewModel.init(this)
        chitChatViewModel.refreshMessages { updateUI() }
    }

    private fun setUpView() {
        refreshButton = findViewById(R.id.button_refresh)
        recyclerView = findViewById(R.id.recyclerView)
        writeMessageButton = findViewById(R.id.imageButton_WriteMessage)
    }

    private fun setUpListeners() {
        refreshButton.setOnClickListener {
            chitChatViewModel.refreshMessages { updateUI() }
        }

        writeMessageButton.setOnClickListener {
            SendMessageDialog.newInstance().show(supportFragmentManager, SendMessageDialog.TAG)
            hideWriteMessageButton()
        }
    }

    private fun hideWriteMessageButton() {
        ObjectAnimator.ofFloat(writeMessageButton, "translationX", 300f).apply {
            duration = 500
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }
    }

    private fun showWriteMessageButton() {
        ObjectAnimator.ofFloat(writeMessageButton, "translationX", 0f).apply {
            duration = 500
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }
    }

    private fun setUpRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        updateUI()
    }

    private fun updateUI() {
        Log.d(TAG, "Updating the UI")

        adapter?.let {
            it.messages = chitChatViewModel.messages
        } ?: run {
            adapter = MessagesAdapter(chitChatViewModel.messages)
        }
        recyclerView.adapter = adapter

        //adapter.notifyDataSetChanged()
    }

    inner class MessageHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var message: Message = Message()
        private var clientTextView: TextView = view.findViewById(R.id.textView_client)
        private var dateTextView: TextView = view.findViewById(R.id.textView_date)
        private var messageTextView: TextView = view.findViewById(R.id.textView_message)
        private var likeTextView: TextView = view.findViewById(R.id.textView_like)
        private var dislikeTextView: TextView = view.findViewById(R.id.textView_dislike)
        private var geoTextView: TextView = view.findViewById(R.id.textView_geo)

        init {
            likeTextView.setOnClickListener {
                if (message.likedByUser == null) {
                    setMessagedLiked(true)
                    updateLikeUI()
                }
            }

            dislikeTextView.setOnClickListener {
                if (message.likedByUser == null) {
                    setMessagedLiked(false)
                    updateLikeUI()
                }
            }
        }

        fun bind(message: Message) {
            this.message = message

            clientTextView.text = message.client
            //val sdf = SimpleDateFormat(resources.getString(R.string.date_pattern))
            dateTextView.text = message.date
            messageTextView.text = message.message
            likeTextView.text = message.likes.toString()
            dislikeTextView.text = message.dislikes.toString()

            if (message.loc[0] != null) {
                geoTextView.text = resources.getString(R.string.location, message.loc[0], message.loc[1]) //"${message.loc[0]}, ${message.loc[1]}"
                geoTextView.visibility = View.VISIBLE
            } else {
                geoTextView.visibility = View.GONE
            }

            updateLikeUI()
        }

        private fun setMessagedLiked(isLiked: Boolean) {

            val result = tryLikeMessage(message._id, isLiked)
            if (!result)
                return

            message.likedByUser = isLiked
            if (isLiked) {
                chitChatViewModel.likeMessage(message._id) { updateUI() }
            } else {
                chitChatViewModel.dislikeMessage(message._id) { updateUI() }
            }
        }

        private fun updateLikeUI() {
            when {
                message.likedByUser == null -> {
                    return
                }
                message.likedByUser!! -> {
                    setDrawablesColorFilter(likeTextView, ResourcesCompat.getColor(resources, R.color.like, null))
                    setDrawablesColorFilter(dislikeTextView, ResourcesCompat.getColor(resources, R.color.grayed_out, null))
                }
                else -> {
                    setDrawablesColorFilter(likeTextView, ResourcesCompat.getColor(resources, R.color.grayed_out, null))
                    setDrawablesColorFilter(dislikeTextView, ResourcesCompat.getColor(resources, R.color.dislike, null))
                }
            }
        }

        private fun setDrawablesColorFilter(tv: TextView, color: Int) {
            for (drawable in tv.compoundDrawables) {
                if (drawable != null) {
                    val mode = PorterDuff.Mode.MULTIPLY
                    drawable.colorFilter = PorterDuffColorFilter(color, mode)
                }
            }
        }
    }

    inner class MessagesAdapter(var messages: Array<Message>) : RecyclerView.Adapter<MessageHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageHolder {
            Log.d(TAG, "MessagesAdapter.onCreateViewHolder(...) Called")
            val view: View = layoutInflater.inflate(R.layout.list_item_message, parent, false)
            return MessageHolder(view)
        }

        override fun onBindViewHolder(holder: MessageHolder, position: Int) {
            Log.d(TAG, "MessagesAdapter.onBindViewHolder(...) Called")
            val message: Message = messages[position]
            holder.bind(message)
        }

        override fun getItemCount(): Int {
            return messages.size
        }

    }

    override fun onMessageSend(msg: String) {
        Log.d(TAG, "On message send: $msg")
        chitChatViewModel.sendMessage(msg, null, null) { updateUI() }
    }

    override fun onMessageDialogClosed() {
        showWriteMessageButton()
    }
}