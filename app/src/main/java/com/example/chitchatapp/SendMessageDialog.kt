package com.example.chitchatapp

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.DialogFragment


class SendMessageDialog : DialogFragment() {
    interface Callbacks {
        fun onMessageSend(msg: String)
        fun onMessageDialogClosed()
    }

    companion object {
        const val TAG = "SendMessageDialog"

        fun newInstance(): SendMessageDialog {
            Log.d(TAG, "Getting new instance of SendMessageDialog")
            return SendMessageDialog()
        }
    }

    var listener: Callbacks? = null

    private lateinit var inputEditText: EditText
    private lateinit var sendButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_send_message_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "Send Message Dialog View Created")

        setupView(view)
        setupListeners()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Verify that the host activity implements the callback interface
        listener = try {
            context as Callbacks
        } catch (e: ClassCastException) {
            throw ClassCastException("$context Must implement onMessageSend")
        }
    }

    override fun onStop() {
        super.onStop()
        listener?.onMessageDialogClosed()
    }

    private fun setupView(view: View) {
        inputEditText = view.findViewById(R.id.editText_Input)
        sendButton = view.findViewById(R.id.button_send)
        sendButton.isEnabled = false
    }

    private fun setupListeners() {
        sendButton.setOnClickListener {
            listener?.onMessageSend(inputEditText.text.toString())
            dismiss()
        }

        inputEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //TODO: Not yet implemented
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                sendButton.isEnabled = s != null && s.isNotEmpty()
            }

            override fun afterTextChanged(s: Editable?) {
                //TODO: Not yet implemented
            }
        })
    }
}