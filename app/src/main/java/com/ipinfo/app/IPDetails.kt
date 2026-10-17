package com.ipinfo.app

import android.os.Parcel
import android.os.Parcelable
import org.json.JSONObject

data class IPDetails(
    val ipAddress: String,
    val hostname: String,
    val provider: String,
    val country: String = "",
    val region: String = "",
    val city: String = "",
    val timestamp: Long = System.currentTimeMillis()
) : Parcelable {
    constructor(parcel: Parcel) : this(
        ipAddress = parcel.readString() ?: "",
        hostname = parcel.readString() ?: "",
        provider = parcel.readString() ?: "",
        country = parcel.readString() ?: "",
        region = parcel.readString() ?: "",
        city = parcel.readString() ?: "",
        timestamp = parcel.readLong()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(ipAddress)
        parcel.writeString(hostname)
        parcel.writeString(provider)
        parcel.writeString(country)
        parcel.writeString(region)
        parcel.writeString(city)
        parcel.writeLong(timestamp)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<IPDetails> {
        override fun createFromParcel(parcel: Parcel): IPDetails {
            return IPDetails(parcel)
        }

        override fun newArray(size: Int): Array<IPDetails?> {
            return arrayOfNulls(size)
        }

        fun fromJson(jsonResponse: JSONObject): IPDetails {
            return IPDetails(
                ipAddress = jsonResponse.optString("ip", ""),
                hostname = jsonResponse.optString("hostname", ""),
                provider = jsonResponse.optString("org", ""),
                country = jsonResponse.optString("country_name", ""),
                region = jsonResponse.optString("region", ""),
                city = jsonResponse.optString("city", "")
            )
        }

        fun empty(ipAddress: String = ""): IPDetails {
            return IPDetails(
                ipAddress = ipAddress,
                hostname = "",
                provider = "",
                country = "",
                region = "",
                city = ""
            )
        }
    }

    fun isValid(): Boolean {
        return ipAddress.isNotEmpty() && country.isNotEmpty()
    }

    fun getFormattedDetails(): String {
        return buildString {
            appendLine("IP: $ipAddress")
            if (hostname.isNotEmpty()) appendLine("Hostname: $hostname")
            if (provider.isNotEmpty()) appendLine("Provider: $provider")
            if (country.isNotEmpty()) appendLine("Țară: $country")
            if (region.isNotEmpty()) appendLine("Regiune: $region")
            if (city.isNotEmpty()) appendLine("Oraș: $city")
        }
    }
}