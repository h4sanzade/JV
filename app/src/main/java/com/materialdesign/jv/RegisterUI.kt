package com.materialdesign.jv

import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.materialdesign.jv.databinding.ActivityLoginUiBinding

class LoginUI : AppCompatActivity() {
    private lateinit var binding: ActivityLoginUiBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityLoginUiBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        val fullText = "Hesabın yoxdur? Qeydiyyatdan keç"
        val spannable = SpannableString(fullText)

        val redColor = ContextCompat.getColor(this, android.R.color.holo_red_dark)

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                Toast.makeText(this@LoginUI, "Qeydiyyatdan keç clicked!", Toast.LENGTH_SHORT).show()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = redColor
                ds.isUnderlineText = false
            }
        }


        val startIndex = fullText.indexOf("Qeydiyyatdan keç")
        val endIndex = startIndex + "Qeydiyyatdan keç".length

        spannable.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.alreadyAcc.text = spannable
        binding.alreadyAcc.movementMethod = LinkMovementMethod.getInstance()
        binding.alreadyAcc.highlightColor = Color.TRANSPARENT

        binding.googleIcon.setOnClickListener {
            Toast.makeText(this, "Google", Toast.LENGTH_SHORT).show()
        }

        binding.icApple.setOnClickListener {
            Toast.makeText(this, "Apple", Toast.LENGTH_SHORT).show()
        }
        binding.icFacebook.setOnClickListener {
            Toast.makeText(this, "Facebook", Toast.LENGTH_SHORT).show()
        }

    }


}