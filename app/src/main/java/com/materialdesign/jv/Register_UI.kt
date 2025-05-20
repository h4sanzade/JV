package com.materialdesign.jv

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.UnderlineSpan
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.materialdesign.jv.databinding.ActivityRegisterUiBinding
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.HttpException
import java.io.IOException

// Error response model
data class ErrorResponse(
    @SerializedName("message") val message: String? = null,
    @SerializedName("error") val error: String? = null,
    @SerializedName("errors") val errors: List<String>? = null
)

class Register_UI : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterUiBinding
    private val gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterUiBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        setupClickableText()
        setupTermsCheckbox()
        setupRegisterButton()
    }


    private fun setupClickableText() {
        val fullText = "Artıq hesabın var? Daxil ol"
        val spannable = SpannableString(fullText)

        val redColor = ContextCompat.getColor(this, android.R.color.holo_red_dark)

        val startIndex = fullText.indexOf("Daxil ol")
        val endIndex = startIndex + "Daxil ol".length

        spannable.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                val intent = Intent(this@Register_UI, LoginUI::class.java)


                val options = ActivityOptionsCompat.makeCustomAnimation(
                    this@Register_UI,
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
                )

                startActivity(intent, options.toBundle())
                finish()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = redColor
                ds.isUnderlineText = true
            }
        }, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.alreadyAcc.text = spannable
        binding.alreadyAcc.movementMethod = LinkMovementMethod.getInstance()
        binding.alreadyAcc.highlightColor = Color.TRANSPARENT
    }





    private fun setupTermsCheckbox() {
        val checkboxText = binding.termsCheckBox.text.toString()
        val termsText = "İstifadəçi Şərtləri və Qaydaları"

        val startIndex = checkboxText.indexOf(termsText)
        if (startIndex != -1) {
            val endIndex = startIndex + termsText.length

            val spannable = SpannableString(checkboxText)


            spannable.setSpan(
                UnderlineSpan(),
                startIndex,
                endIndex,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )


            val clickableSpan = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    showTermsAndConditions()
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)

                    ds.isUnderlineText = true
                }
            }

            spannable.setSpan(
                clickableSpan,
                startIndex,
                endIndex,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            binding.termsCheckBox.text = spannable
            binding.termsCheckBox.movementMethod = LinkMovementMethod.getInstance()
            binding.termsCheckBox.highlightColor = Color.TRANSPARENT
        }
    }



    private fun showTermsAndConditions() {
        val termsFragment = TermsFragment()
        termsFragment.show(supportFragmentManager, TermsFragment.TAG)
    }

    private fun setupRegisterButton() {
        binding.registerButton.setOnClickListener {

            if (!binding.termsCheckBox.isChecked) {
                Toast.makeText(
                    this,
                    "Zəhmət olmasa, İstifadəçi Şərtləri və Qaydaları ilə razılaşın",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val email = binding.emailInputEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            val confirmPassword = binding.confirmPasswordEditText.text.toString()
            val phoneNumber = binding.phoneNumberInputEditText.text.toString()


            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || phoneNumber.isEmpty()) {
                Toast.makeText(this, "Zəhmət olmasa, bütün sahələri doldurun", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            if (password != confirmPassword) {
                Toast.makeText(this, "Şifrələr uyğun gəlmir", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            performSignUp(email, password, confirmPassword, phoneNumber)
        }
    }

    private fun performSignUp(email: String, password: String, confirmPassword: String, phoneNumber: String) {
        binding.registerButton.isEnabled = false

        lifecycleScope.launch {
            try {
                val signUpRequest = SignUpRequest(
                    phoneNumber = phoneNumber,
                    email = email,
                    password = password,
                    confirmPassword = confirmPassword
                )

                val response = RetrofitInstance.api.signUp(signUpRequest)

                if (response.isSuccessful && response.body() != null) {
                    val token = response.body()!!.data.token
                    saveTokenToSharedPreferences(token)

                    Toast.makeText(
                        this@Register_UI,
                        "Qeydiyyat uğurla tamamlandı!",
                        Toast.LENGTH_SHORT
                    ).show()

                    val intent = Intent(this@Register_UI, LoginUI::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    val errorMessage = parseErrorResponse(response.errorBody())
                    Toast.makeText(
                        this@Register_UI,
                        errorMessage,
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: IOException) {
                Toast.makeText(
                    this@Register_UI,
                    "Şəbəkə xətası. İnternet bağlantısını yoxlayın.",
                    Toast.LENGTH_SHORT
                ).show()
            } catch (e: HttpException) {

                val errorMessage = parseErrorResponse(e.response()?.errorBody())
                Toast.makeText(
                    this@Register_UI,
                    errorMessage,
                    Toast.LENGTH_LONG
                ).show()
            } catch (e: Exception) {
                Toast.makeText(
                    this@Register_UI,
                    "Gözlənilməz xəta baş verdi: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            } finally {

                binding.registerButton.isEnabled = true
            }
        }
    }


    private fun parseErrorResponse(errorBody: ResponseBody?): String {
        return try {
            if (errorBody != null) {
                val errorResponse = gson.fromJson(errorBody.string(), ErrorResponse::class.java)
                when {

                    !errorResponse.message.isNullOrEmpty() -> errorResponse.message
                    !errorResponse.error.isNullOrEmpty() -> errorResponse.error
                    !errorResponse.errors.isNullOrEmpty() -> errorResponse.errors.joinToString("\n")
                    else -> "Qeydiyyat uğursuz oldu: Naməlum xəta"
                }
            } else {
                "Qeydiyyat uğursuz oldu: Naməlum xəta"
            }
        } catch (e: Exception) {
            "Qeydiyyat uğursuz oldu: Xəta mesajı işlənərkən problem yarandı"
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
