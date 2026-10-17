package com.ipinfo.app

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.ComponentActivity
import kotlinx.coroutines.*
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class CountryInfoActivity : ComponentActivity() {
    private lateinit var ipTextView: TextView
    private lateinit var infoTextView: TextView
    private lateinit var loadingIndicator: ProgressBar
    private lateinit var historyListView: ListView
    private lateinit var backButton: Button

    private lateinit var adapter: IPHistoryAdapter
    private val ipHistoryItems = mutableListOf<IPHistoryItem>()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + Job())
    private var currentIPDetails: IPDetails? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_country_info)

        initializeViews()
        processIncomingData()
    }

    private fun initializeViews() {
        ipTextView = findViewById(R.id.textViewIP)
        infoTextView = findViewById(R.id.textViewCountryInfo)
        loadingIndicator = findViewById(R.id.progressBarLoading)
        historyListView = findViewById(R.id.listViewHistory)
        backButton = findViewById(R.id.buttonBack)
        backButton.setOnClickListener { finish() }

        adapter = IPHistoryAdapter(this, ipHistoryItems)
        historyListView.adapter = adapter
    }

    private fun processIncomingData() {
        val ipDetails = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("IP_DETAILS", IPDetails::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("IP_DETAILS")
        }

        val ip = ipDetails?.ipAddress ?: intent.getStringExtra("IP_ADDRESS") ?: run {
            handleInvalidInput()
            return
        }

        when {
            ipDetails != null -> displayIPDetails(ipDetails)
            validateIP(ip) -> {
                setupInitialUI(ip)
                fetchIPInfo(ip)
            }
            else -> handleInvalidInput()
        }
    }

    private fun setupInitialUI(ip: String) {
        ipTextView.text = getString(R.string.country_info_title, ip)
        infoTextView.text = getString(R.string.loading)
        loadingIndicator.visibility = View.VISIBLE

        val initialHistoryItem = IPHistoryItem(
            ipAddress = ip,
            location = getString(R.string.loading),
            timestamp = System.currentTimeMillis(),
            isValid = validateIP(ip),
            reliability = if(validateIP(ip)) 5.0f else 1.0f
        )
        ipHistoryItems.add(initialHistoryItem)
        adapter.notifyDataSetChanged()
    }

    private fun displayIPDetails(ipDetails: IPDetails) {
        currentIPDetails = ipDetails
        ipTextView.text = getString(R.string.country_info_title, ipDetails.ipAddress)
        loadingIndicator.visibility = View.GONE

        infoTextView.apply {
            text = ipDetails.getFormattedDetails()
            visibility = View.VISIBLE
        }

        updateHistoryWithDetails(ipDetails)
    }

    private fun updateHistoryWithDetails(ipDetails: IPDetails) {
        val historyItem = IPHistoryItem(
            ipAddress = ipDetails.ipAddress,
            location = buildLocationString(ipDetails),
            timestamp = ipDetails.timestamp,
            isValid = ipDetails.isValid(),
            reliability = calculateReliability(ipDetails)
        )

        if (ipHistoryItems.isNotEmpty()) {
            ipHistoryItems[0] = historyItem
        } else {
            ipHistoryItems.add(historyItem)
        }
        adapter.notifyDataSetChanged()
    }

    private fun buildLocationString(ipDetails: IPDetails): String {
        return when {
            ipDetails.country.isNotEmpty() && ipDetails.city.isNotEmpty() ->
                "${ipDetails.country}, ${ipDetails.city}"
            ipDetails.country.isNotEmpty() ->
                ipDetails.country
            else ->
                getString(R.string.unknown_location)
        }
    }

    private fun calculateReliability(ipDetails: IPDetails): Float {
        return when {
            !ipDetails.isValid() -> 1.0f
            ipDetails.city.isEmpty() -> 3.0f
            else -> 5.0f
        }
    }

    private fun fetchIPInfo(ip: String) {
        coroutineScope.launch(Dispatchers.IO) {
            try {
                val jsonResponse = fetchJsonFromApi(ip)
                val ipDetails = IPDetails.fromJson(jsonResponse)

                withContext(Dispatchers.Main) {
                    displayIPDetails(ipDetails)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    handleError(e)
                }
            }
        }
    }

    private fun fetchJsonFromApi(ip: String): JSONObject {
        return if (validateIP(ip)) {
            val url = URL("https://ipapi.co/$ip/json/")
            val connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 5000
                readTimeout = 5000
            }

            connection.inputStream.bufferedReader().use {
                JSONObject(it.readText())
            }
        } else {
            throw IllegalArgumentException(getString(R.string.error_invalid_ip))
        }
    }

    private fun validateIP(ip: String): Boolean {
        val regex = Regex(
            "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$"
        )
        return ip.matches(regex)
    }

    private fun handleInvalidInput() {
        loadingIndicator.visibility = View.GONE
        infoTextView.apply {
            text = getString(R.string.error_invalid_ip)
            visibility = View.VISIBLE
        }
        Toast.makeText(this, getString(R.string.error_invalid_ip), Toast.LENGTH_LONG).show()
    }

    private fun handleError(error: Exception) {
        loadingIndicator.visibility = View.GONE
        val errorMessage = getString(R.string.error_fetching_data, error.message ?: "Unknown error")

        infoTextView.apply {
            text = errorMessage
            visibility = View.VISIBLE
        }

        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }
}