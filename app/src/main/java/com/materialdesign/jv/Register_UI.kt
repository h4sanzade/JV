package com.materialdesign.jv

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
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

        // Since your Register UI layout is currently empty, I'm assuming you'll add the necessary fields
        // This is a placeholder for the registration button click listener
        // You'll need to update your activity_register_ui.xml with proper UI elements

        setupRegisterButton()
    }

    private fun setupRegisterButton() {
        // This is a placeholder - you'll need to add a register button to your layout
        // binding.registerButton.setOnClickListener {
        //     val username = binding.usernameEditText.text.toString()
        //     val password = binding.passwordEditText.text.toString()
        //     val phoneNumber = binding.phoneNumberEditText.text.toString()
        //
        //     if (username.isEmpty() || password.isEmpty() || phoneNumber.isEmpty()) {
        //         Toast.makeText(this, "Zəhmət olmasa, bütün sahələri doldurun", Toast.LENGTH_SHORT).show()
        //         return@setOnClickListener
        //     }
        //
        //     performSignUp(username, password, phoneNumber)
        // }
    }

    private fun performSignUp(username: String, password: String, phoneNumber: String) {
        // Disable register button to prevent multiple requests
        // binding.registerButton.isEnabled = false

        lifecycleScope.launch {
            try {
                val signUpRequest = SignUpRequest(
                    username = username,
                    password = password,
                    phoneNumber = phoneNumber
                )

                val response = RetrofitInstance.api.signUp(signUpRequest)

                if (response.isSuccessful && response.body() != null) {
                    // Save token
                    val token = response.body()!!.data.token
                    saveTokenToSharedPreferences(token)

                    Toast.makeText(
                        this@Register_UI,
                        "Registration successful!",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Navigate to main activity
                    val intent = Intent(this@Register_UI, MainActivity::class.java)
                    startActivity(intent)
                    finish() // Close registration activity
                } else {
                    // Handle unsuccessful response
                    Toast.makeText(
                        this@Register_UI,
                        "Registration failed: ${response.message() ?: "Unknown error"}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: IOException) {
                Toast.makeText(
                    this@Register_UI,
                    "Network error. Please check your connection.",
                    Toast.LENGTH_SHORT
                ).show()
            } catch (e: HttpException) {
                Toast.makeText(
                    this@Register_UI,
                    "API error ${e.code()}: ${e.message()}",
                    Toast.LENGTH_SHORT
                ).show()
            } catch (e: Exception) {
                Toast.makeText(
                    this@Register_UI,
                    "An unexpected error occurred: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            } finally {
                // Re-enable register button
                // binding.registerButton.isEnabled = true
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