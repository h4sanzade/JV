package com.materialdesign.jv

import android.content.Intent
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
import androidx.lifecycle.lifecycleScope
import com.materialdesign.jv.databinding.ActivityLoginUiBinding
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class LoginUI : AppCompatActivity() {
    private lateinit var binding: ActivityLoginUiBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityLoginUiBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        setupClickableText()
        setupSocialButtons()
        setupLoginButton()
    }

    private fun setupClickableText() {
        val fullText = "Hesabın yoxdur? Qeydiyyatdan keç"
        val spannable = SpannableString(fullText)

        val redColor = ContextCompat.getColor(this, android.R.color.holo_red_dark)

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                // Navigate to register activity with animation
                val intent = Intent(this@LoginUI, Register_UI::class.java)
                startActivity(intent)

                // Apply custom transition animation
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = redColor
                ds.isUnderlineText = true
            }
        }

        val startIndex = fullText.indexOf("Qeydiyyatdan keç")
        val endIndex = startIndex + "Qeydiyyatdan keç".length

        spannable.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.alreadyAcc.text = spannable
        binding.alreadyAcc.movementMethod = LinkMovementMethod.getInstance()
        binding.alreadyAcc.highlightColor = Color.TRANSPARENT
    }

    private fun setupSocialButtons() {
        binding.googleIcon.setOnClickListener {
            Toast.makeText(this, "Google authentication not implemented yet", Toast.LENGTH_SHORT).show()
        }

        binding.icApple.setOnClickListener {
            Toast.makeText(this, "Apple authentication not implemented yet", Toast.LENGTH_SHORT).show()
        }

        binding.icFacebook.setOnClickListener {
            Toast.makeText(this, "Facebook authentication not implemented yet", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupLoginButton() {
        binding.signUp.setOnClickListener {
            val phoneNumber = binding.emailInputEditText.text.toString()
            val password = binding.passEditText.text.toString()

            if (phoneNumber.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Zəhmət olmasa, bütün sahələri doldurun", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Assuming phoneNumber is used as username
            performLogin(phoneNumber, password)
        }
    }

    private fun performLogin(username: String, password: String) {
        binding.signUp.isEnabled = false

        lifecycleScope.launch {
            try {
                val response = RetrofitInstance.api.login(LoginRequest(username, password))

                if (response.isSuccessful && response.body() != null) {
                    // Save token for future use
                    val token = response.body()!!.data.token
                    saveTokenToSharedPreferences(token)

                    // Navigate to main activity
                    val intent = Intent(this@LoginUI, MainActivity::class.java)
                    startActivity(intent)
                    finish() // Close login activity
                } else {
                    // Handle unsuccessful response
                    Toast.makeText(
                        this@LoginUI,
                        "Login failed: ${response.message() ?: "Unknown error"}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: IOException) {
                Toast.makeText(
                    this@LoginUI,
                    "Network error. Please check your connection.",
                    Toast.LENGTH_SHORT
                ).show()
            } catch (e: HttpException) {
                Toast.makeText(
                    this@LoginUI,
                    "API error ${e.code()}: ${e.message()}",
                    Toast.LENGTH_SHORT
                ).show()
            } catch (e: Exception) {
                Toast.makeText(
                    this@LoginUI,
                    "An unexpected error occurred: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            } finally {
                binding.signUp.isEnabled = true
            }
        }
    }


    private fun saveTokenToSharedPreferences(token: String) {
        val sharedPrefs = getSharedPreferences("auth_prefs", MODE_PRIVATE)
        sharedPrefs.edit().apply {
            putString("auth_token", token)
            apply()
        }
    }
}