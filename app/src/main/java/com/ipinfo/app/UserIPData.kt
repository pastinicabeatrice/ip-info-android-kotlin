package com.ipinfo.app

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserIPData(
    val ipAddress: String,
    val userName: String,
    val timeStamp: Long
) : Parcelable
