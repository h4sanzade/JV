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
import com.materialdesign.jv.databinding.ActivityRegisterUiBinding
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class Register_UI : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterUiBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterUiBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        setupClickableText()
        setupRegisterButton()
    }

    private fun setupClickableText() {
        val fullText = "Artıq hesabın var? Daxil ol"
        val spannable = SpannableString(fullText)

        val redColor = ContextCompat.getColor(this, android.R.color.holo_red_dark)

        // Rəng span əlavə edirik
        val startIndex = fullText.indexOf("Daxil ol")
        val endIndex = startIndex + "Daxil ol".length

        // ClickListener verəcəyik
        spannable.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                val intent = Intent(this@Register_UI, LoginUI::class.java)
                startActivity(intent)
                finish() // Close the Register activity when navigating to Login
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = redColor // Qırmızı rəng
                ds.isUnderlineText = true  // Altı xət çəkilməsin
            }
        }, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.alreadyAcc.text = spannable
        binding.alreadyAcc.movementMethod = LinkMovementMethod.getInstance()
        binding.alreadyAcc.highlightColor = Color.TRANSPARENT
    }

    private fun setupRegisterButton() {
        binding.registerButton.setOnClickListener {
            val email = binding.emailInputEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            val confirmPassword = binding.confirmPasswordEditText.text.toString()
            val phoneNumber = binding.phoneNumberInputEditText.text.toString()

            // Check if any field is empty
            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || phoneNumber.isEmpty()) {
                Toast.makeText(this, "Zəhmət olmasa, bütün sahələri doldurun", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Check if passwords match
            if (password != confirmPassword) {
                Toast.makeText(this, "Şifrələr uyğun gəlmir", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // If everything is valid, proceed with registration
            performSignUp(email, password, phoneNumber,confirmPassword)
        }
    }

    private fun performSignUp(username: String, password: String, phoneNumber: String,confirmPassword:String) {
        // Disable register button to prevent multiple requests
        binding.registerButton.isEnabled = false

        lifecycleScope.launch {
            try {
                val signUpRequest = SignUpRequest(
                    username = username,
                    password = password,
                    phoneNumber = phoneNumber,
                    confirmPassword = confirmPassword
                )

                val response = RetrofitInstance.api.signUp(signUpRequest)

                if (response.isSuccessful && response.body() != null) {
                    // Save token
                    val token = response.body()!!.data.token
                    saveTokenToSharedPreferences(token)

                    Toast.makeText(
                        this@Register_UI,
                        "Qeydiyyat uğurla tamamlandı!",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Navigate to login activity
                    val intent = Intent(this@Register_UI, LoginUI::class.java)
                    startActivity(intent)
                    finish() // Close registration activity
                } else {
                    // Handle unsuccessful response
                    Toast.makeText(
                        this@Register_UI,
                        "Qeydiyyat uğursuz oldu: ${response.message() ?: "Naməlum xəta"}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: IOException) {
                Toast.makeText(
                    this@Register_UI,
                    "Şəbəkə xətası. İnternet bağlantısını yoxlayın.",
                    Toast.LENGTH_SHORT
                ).show()
            } catch (e: HttpException) {
                Toast.makeText(
                    this@Register_UI,
                    "API xətası ${e.code()}: ${e.message()}",
                    Toast.LENGTH_SHORT
                ).show()
            } catch (e: Exception) {
                Toast.makeText(
                    this@Register_UI,
                    "Gözlənilməz xəta baş verdi: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            } finally {
                // Re-enable register button
                binding.registerButton.isEnabled = true
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