package com.materialdesign.jv

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ScrollView
import android.widget.TextView
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.DialogFragment
import com.materialdesign.jv.R

class TermsFragment : DialogFragment() {

    private lateinit var closeButton: Button
    private lateinit var scrollView: ScrollView
    private lateinit var termsContent: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Use the full dialog theme to make it wider
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_terms, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize views
        closeButton = view.findViewById(R.id.close_button)
        scrollView = view.findViewById(R.id.terms_scroll_view)
        termsContent = view.findViewById(R.id.terms_content)

        // Initially disable the close button
        setCloseButtonEnabled(false)

        // Set up scroll listener to detect when user has scrolled to the bottom
        scrollView.viewTreeObserver.addOnGlobalLayoutListener {
            setupScrollListener()
        }

        // Set up close button
        closeButton.setOnClickListener {
            dismiss()
        }
    }

    private fun setupScrollListener() {
        scrollView.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            // Check if the user has scrolled to the bottom
            val isAtBottom = scrollY + scrollView.height >= termsContent.height

            // Enable the close button only if user has scrolled to the bottom
            setCloseButtonEnabled(isAtBottom)
        }
    }

    private fun setCloseButtonEnabled(enabled: Boolean) {
        closeButton.isEnabled = enabled
        if (enabled) {
            closeButton.alpha = 1.0f  // Full opacity when enabled
        } else {
            closeButton.alpha = 0.3f  // Half opacity when disabled
        }
        // Remove the background resource changes to keep the original red color
    }

    override fun onStart() {
        super.onStart()
        // Make the dialog take up most of the screen width
        dialog?.window?.apply {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.WRAP_CONTENT
            setLayout(width, height)
        }
    }

    companion object {
        const val TAG = "TermsFragmentDialog"
    }
}