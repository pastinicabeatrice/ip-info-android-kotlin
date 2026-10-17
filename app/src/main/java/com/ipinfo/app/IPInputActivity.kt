package com.ipinfo.app

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.activity.ComponentActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class IPInputActivity : ComponentActivity() {
    private lateinit var ipInputLayout: TextInputLayout
    private lateinit var ipInputEditText: TextInputEditText
    private lateinit var submitButton: Button
    private lateinit var backButton: Button
    private lateinit var errorTextView: TextView
    private lateinit var titleTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ip_input)

        initializeViews()
        setupListeners()
    }

    private fun initializeViews() {
        ipInputLayout = findViewById(R.id.textInputLayout)
        ipInputEditText = findViewById(R.id.editTextIP)
        submitButton = findViewById(R.id.buttonSubmit)
        backButton = findViewById(R.id.buttonBack)
        errorTextView = findViewById(R.id.textViewError)
        titleTextView = findViewById(R.id.textViewTitle)

        errorTextView.visibility = View.GONE
    }

    private fun setupListeners() {
        submitButton.setOnClickListener {
            val ip = ipInputEditText.text?.toString()?.trim() ?: ""
            validateAndSubmitIP(ip)
        }

        backButton.setOnClickListener {
            finish()
        }

        ipInputEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                ipInputLayout.error = null
                errorTextView.visibility = View.GONE
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun validateAndSubmitIP(ip: String) {
        when {
            ip.isBlank() -> {
                showError(getString(R.string.error_empty_field))
            }
            !validateIP(ip) -> {
                showError(getString(R.string.error_invalid_ip))
            }
            else -> {
                val intent = Intent(this, CountryInfoActivity::class.java)
                intent.putExtra("IP_ADDRESS", ip)
                startActivity(intent)
            }
        }
    }

    private fun showError(message: String) {
        errorTextView.apply {
            text = message
            visibility = View.VISIBLE
        }
        ipInputLayout.error = message
    }

    private fun validateIP(ip: String): Boolean {
        val regex = Regex(
            "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$"
        )
        return ip.matches(regex)
    }
}