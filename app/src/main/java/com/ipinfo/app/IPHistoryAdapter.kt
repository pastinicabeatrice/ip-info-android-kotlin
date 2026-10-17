package com.ipinfo.app

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.*

class IPHistoryAdapter(
    context: Context,
    items: List<IPHistoryItem>
) : ArrayAdapter<IPHistoryItem>(context, R.layout.ip_history_item, items) {
    private val dateFormatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val viewHolder: ViewHolder
        val view: View

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.ip_history_item, parent, false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        getItem(position)?.let { item ->
            bindViewHolder(viewHolder, item)
        }

        return view
    }

    private fun bindViewHolder(holder: ViewHolder, item: IPHistoryItem) {
        holder.statusIcon.apply {
            setImageResource(android.R.drawable.presence_online)
            setColorFilter(
                ContextCompat.getColor(
                    context,
                    if (item.isValid) R.color.valid_ip else R.color.invalid_ip
                )
            )
        }

        holder.ipAddressText.apply {
            text = item.ipAddress
            setTextColor(ContextCompat.getColor(context, android.R.color.primary_text_light))
        }

        holder.locationText.apply {
            text = item.location
            setTextColor(ContextCompat.getColor(context, android.R.color.secondary_text_light))
        }

        holder.timestampText.apply {
            text = dateFormatter.format(Date(item.timestamp))
            setTextColor(ContextCompat.getColor(context, android.R.color.secondary_text_light))
        }

        holder.reliabilityRating.apply {
            rating = item.reliability
            isEnabled = false
        }
    }

    private class ViewHolder(view: View) {
        val statusIcon: ImageView = view.findViewById(R.id.statusIcon)
        val ipAddressText: TextView = view.findViewById(R.id.ipAddressText)
        val locationText: TextView = view.findViewById(R.id.locationText)
        val timestampText: TextView = view.findViewById(R.id.timestampText)
        val reliabilityRating: RatingBar = view.findViewById(R.id.reliabilityRating)
    }
}