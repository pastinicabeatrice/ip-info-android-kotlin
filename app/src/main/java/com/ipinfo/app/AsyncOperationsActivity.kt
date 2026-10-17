package com.ipinfo.app

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.ComponentActivity
import androidx.cardview.widget.CardView
import kotlinx.coroutines.*
import java.util.*

class AsyncOperationsActivity : ComponentActivity() {
    private lateinit var progressBar: ProgressBar
    private lateinit var progressText: TextView
    private lateinit var dateButton: Button
    private lateinit var startButton: Button
    private lateinit var statusCard: CardView
    private lateinit var scheduledDateText: TextView
    private lateinit var titleText: TextView

    private var selectedDate: Long? = null
    private val coroutineScope = CoroutineScope(Dispatchers.Main + Job())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_async_operations)

        initializeViews()
        setupListeners()
    }

    private fun initializeViews() {
        progressBar = findViewById(R.id.progressBar)
        progressText = findViewById(R.id.textViewProgress)
        dateButton = findViewById(R.id.buttonSelectDate)
        startButton = findViewById(R.id.buttonStart)
        statusCard = findViewById(R.id.cardStatus)
        scheduledDateText = findViewById(R.id.textViewScheduledDate)
        titleText = findViewById(R.id.textViewTitle)

        progressBar.max = 100
        progressBar.progress = 0
        startButton.isEnabled = false
        statusCard.visibility = View.GONE
        progressText.text = getString(R.string.async_progress, 0)
    }

    private fun setupListeners() {
        dateButton.setOnClickListener {
            showDatePicker()
        }

        startButton.setOnClickListener {
            startAsyncOperation()
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            this,
            { _, year, month, day ->
                calendar.set(year, month, day)
                selectedDate = calendar.timeInMillis
                updateScheduledDateDisplay()
                startButton.isEnabled = true
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun updateScheduledDateDisplay() {
        selectedDate?.let { date ->
            statusCard.visibility = View.VISIBLE
            val formattedDate = android.text.format.DateFormat
                .getDateFormat(applicationContext)
                .format(Date(date))
            scheduledDateText.text = getString(R.string.async_scheduled, formattedDate)
        }
    }

    private fun startAsyncOperation() {
        setControlsEnabled(false)

        coroutineScope.launch(Dispatchers.IO) {
            try {
                selectedDate?.let { startTime ->
                    val currentTime = System.currentTimeMillis()
                    if (startTime > currentTime) {
                        delay(startTime - currentTime)
                    }

                    for (i in 0..100 step 10) {
                        withContext(Dispatchers.Main) {
                            updateProgress(i)
                        }
                        delay(500)
                    }

                    withContext(Dispatchers.Main) {
                        setControlsEnabled(true)
                        showCompletionMessage()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    handleError(e)
                }
            }
        }
    }

    private fun updateProgress(progress: Int) {
        progressBar.progress = progress
        progressText.text = getString(R.string.async_progress, progress)
    }

    private fun setControlsEnabled(enabled: Boolean) {
        startButton.isEnabled = enabled
        dateButton.isEnabled = enabled
    }

    private fun showCompletionMessage() {
        Toast.makeText(
            this,
            getString(R.string.async_operation_complete),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun handleError(error: Exception) {
        setControlsEnabled(true)
        Toast.makeText(
            this,
            getString(R.string.async_error, error.message),
            Toast.LENGTH_LONG
        ).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }
}