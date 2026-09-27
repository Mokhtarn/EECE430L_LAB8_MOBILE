package com.mokhtar.currencyexchange

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.mokhtar.currencyexchange.api.Authentication
import com.mokhtar.currencyexchange.api.ExchangeService
import com.mokhtar.currencyexchange.api.model.Token
import com.mokhtar.currencyexchange.api.model.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private var usernameEditText: TextInputLayout? = null
    private var passwordEditText: TextInputLayout? = null
    private var submitButton: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        usernameEditText = findViewById(R.id.txtInptUsername)
        passwordEditText = findViewById(R.id.txtInptPassword)
        submitButton = findViewById(R.id.btnSubmit)

        submitButton?.setOnClickListener {
            authenticate()
        }
    }

    private fun authenticate() {
        val user = User().apply {
            username = usernameEditText?.editText?.text.toString()
            password = passwordEditText?.editText?.text.toString()
        }

        submitButton?.isEnabled = false
        ExchangeService.exchangeApi().authenticate(user).enqueue(object : Callback<Token> {
            override fun onFailure(call: Call<Token>, t: Throwable) {
                submitButton?.isEnabled = true
                showMessage("Could not log in.")
            }

            override fun onResponse(call: Call<Token>, response: Response<Token>) {
                val token = response.body()?.token
                if (!response.isSuccessful || token == null) {
                    submitButton?.isEnabled = true
                    showMessage("Invalid username or password.")
                    return
                }

                Authentication.saveToken(token)
                onCompleted()
            }
        })
    }

    private fun showMessage(message: String) {
        Snackbar.make(submitButton as View, message, Snackbar.LENGTH_LONG).show()
    }

    private fun onCompleted() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }
}
