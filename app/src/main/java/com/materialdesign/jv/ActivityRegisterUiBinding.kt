package com.materialdesign.jv.databinding

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.materialdesign.jv.R

class ActivityRegisterUiBinding private constructor(private val rootView: View) : ViewBinding {

    // You'll need to add references to your UI elements here once you update your layout
    // For example:
    // val usernameEditText: EditText = rootView.findViewById(R.id.usernameEditText)
    // val passwordEditText: EditText = rootView.findViewById(R.id.passwordEditText)
    // val phoneNumberEditText: EditText = rootView.findViewById(R.id.phoneNumberEditText)
    // val registerButton: Button = rootView.findViewById(R.id.registerButton)

    override fun getRoot(): View = rootView

    companion object {
        fun inflate(inflater: LayoutInflater, parent: ViewGroup? = null, attachToParent: Boolean = false): ActivityRegisterUiBinding {
            val root = inflater.inflate(R.layout.activity_register_ui, parent, attachToParent)
            return ActivityRegisterUiBinding(root)
        }

        fun inflate(inflater: LayoutInflater): ActivityRegisterUiBinding {
            return inflate(inflater, null, false)
        }
    }
}