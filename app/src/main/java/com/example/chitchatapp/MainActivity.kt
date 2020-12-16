package com.example.chitchatapp

import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

const val TAG = "MAIN_ACTIVITY"

class MainActivity : AppCompatActivity() {

    private val chitChatViewModel: ChitChatViewModel by lazy {
        return@lazy ChitChatViewModel()
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MessagesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG, "On Create")

        for (x in 0..10)
            chitChatViewModel.messages.add(Message())

        setUpView()
        setUpListeners()
        setUpRecyclerView()
    }

    private fun setUpView() {
        recyclerView = findViewById(R.id.recyclerView)
    }

    private fun setUpListeners() {

    }

    private fun setUpRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        adapter = MessagesAdapter(chitChatViewModel.messages)
        recyclerView.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    inner class MessageHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var messageLikeState: MessageLikeState = MessageLikeState.Neither
        private var message: Message = Message()
        private var clientTextView: TextView = view.findViewById(R.id.textView_client)
        private var messageTextView: TextView = view.findViewById(R.id.textView_message)
        private var likeTextView: TextView = view.findViewById(R.id.textView_like)
        private var dislikeTextView: TextView = view.findViewById(R.id.textView_dislike)

        init {
            likeTextView.setOnClickListener {
                if (messageLikeState == MessageLikeState.Liked) {
                    setMessagedLiked(MessageLikeState.Neither)
                } else {
                    setMessagedLiked(MessageLikeState.Liked)
                }
            }

            dislikeTextView.setOnClickListener {
                if (messageLikeState == MessageLikeState.Disliked) {
                    setMessagedLiked(MessageLikeState.Neither)
                } else {
                    setMessagedLiked(MessageLikeState.Disliked)
                }
            }
        }

        fun bind(message: Message) {
            this.message = message

            // Do Stuff
        }

        private fun setMessagedLiked(likeState: MessageLikeState) {
            messageLikeState = likeState
            when (messageLikeState) {
                MessageLikeState.Liked -> {
                    setDrawablesColorFilter(likeTextView, ResourcesCompat.getColor(resources, R.color.like, null))
                    setDrawablesColorFilter(dislikeTextView, ResourcesCompat.getColor(resources, R.color.grayed_out, null))
                }
                MessageLikeState.Disliked -> {
                    setDrawablesColorFilter(likeTextView, ResourcesCompat.getColor(resources, R.color.grayed_out, null))
                    setDrawablesColorFilter(dislikeTextView, ResourcesCompat.getColor(resources, R.color.dislike, null))
                }
                MessageLikeState.Neither -> {
                    setDrawablesColorFilter(likeTextView, ResourcesCompat.getColor(resources, R.color.grayed_out, null))
                    setDrawablesColorFilter(dislikeTextView, ResourcesCompat.getColor(resources, R.color.grayed_out, null))
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

    inner class MessagesAdapter(var messages: List<Message>) : RecyclerView.Adapter<MessageHolder>() {

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

    enum class MessageLikeState {
        Neither,
        Liked,
        Disliked
    }
}