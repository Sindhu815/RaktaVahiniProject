package com.healthcare.raktavahini.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.healthcare.raktavahini.data.model.DonationLog
import com.healthcare.raktavahini.databinding.ItemDonationLogBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DonationLogAdapter(
    private var logs: List<DonationLog>
) : RecyclerView.Adapter<DonationLogAdapter.ViewHolder>() {
    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    inner class ViewHolder(
        private val binding: ItemDonationLogBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(log: DonationLog) {
            binding.tvLogBloodGroup.text = log.bloodGroup
            binding.tvLogTitle.text = log.donorName
            binding.tvLogLocation.text = log.location
            binding.tvLogDate.text = dateFormat.format(Date(log.donatedAt))
            binding.tvLogNote.text = log.note.ifBlank { "Donation recorded" }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDonationLogBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(logs[position])
    }

    override fun getItemCount(): Int = logs.size

    fun updateLogs(newLogs: List<DonationLog>) {
        logs = newLogs
        notifyDataSetChanged()
    }
}
