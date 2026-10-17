package com.ipinfo.app

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import org.json.JSONObject

class JsonProcessingActivity : AppCompatActivity() {
    private lateinit var processingTypeLayout: TextInputLayout
    private lateinit var processingTypeDropdown: AutoCompleteTextView
    private lateinit var autoProcessCheckbox: CheckBox
    private lateinit var complexitySeekBar: SeekBar
    private lateinit var ratingText: TextView
    private lateinit var ratingValueText: TextView
    private lateinit var processButton: Button
    private lateinit var resultText: TextView

    private val processingOptions = listOf("Basic", "Detailed", "Advanced")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_json_processing)

        initializeViews()
        setupProcessingTypeDropdown()
        setupListeners()
    }

    private fun initializeViews() {
        processingTypeLayout = findViewById(R.id.processingTypeLayout)
        processingTypeDropdown = findViewById(R.id.processingTypeDropdown)
        autoProcessCheckbox = findViewById(R.id.autoProcessCheckbox)
        complexitySeekBar = findViewById(R.id.complexitySeekBar)
        ratingText = findViewById(R.id.ratingText)
        ratingValueText = findViewById(R.id.ratingValueText)
        processButton = findViewById(R.id.processButton)
        resultText = findViewById(R.id.resultText)

        complexitySeekBar.max = 5
        complexitySeekBar.progress = 3
        updateRatingText(3)
    }

    private fun setupProcessingTypeDropdown() {
        val adapter = ArrayAdapter(this, R.layout.dropdown_item, processingOptions)
        processingTypeDropdown.setAdapter(adapter)
        processingTypeDropdown.setText(processingOptions[0], false)
    }

    private fun setupListeners() {
        processButton.setOnClickListener {
            resultText.text = processJsonData()
        }

        complexitySeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                updateRatingText(progress)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        autoProcessCheckbox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                resultText.text = processJsonData()
            }
        }
    }

    private fun updateRatingText(progress: Int) {
        ratingValueText.text = getString(R.string.rating_format, progress)
    }

    private fun processJsonData(): String {
        return "Procesare JSON completă"
    }
}