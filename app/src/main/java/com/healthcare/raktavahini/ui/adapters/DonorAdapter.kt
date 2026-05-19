package com.healthcare.raktavahini.ui.adapters

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.healthcare.raktavahini.R
import com.healthcare.raktavahini.data.model.Donor
import com.healthcare.raktavahini.databinding.ItemDonorBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DonorAdapter(
    private var donors: List<Donor>,
    private val onDonorSelected: (Donor) -> Unit
) : RecyclerView.Adapter<DonorAdapter.ViewHolder>() {
    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    inner class ViewHolder(
        private val binding: ItemDonorBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(donor: Donor) {
            val context = binding.root.context

            binding.tvName.text = donor.name
            binding.tvBloodGroup.text = donor.bloodGroup
            binding.tvLocation.text = donor.location
            binding.tvLastDonation.text = "Last donated ${dateFormat.format(Date(donor.lastDonationDate))}"

            if (donor.isEligible()) {
                binding.tvEligibility.text = "Eligible"
                binding.tvEligibility.setTextColor(ContextCompat.getColor(context, R.color.rakta_green))
                binding.tvEligibility.setBackgroundResource(R.drawable.badge_eligible)
            } else {
                binding.tvEligibility.text = "Not eligible"
                binding.tvEligibility.setTextColor(ContextCompat.getColor(context, R.color.rakta_red))
                binding.tvEligibility.setBackgroundResource(R.drawable.badge_unavailable)
            }

            binding.btnCall.setOnClickListener {
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${donor.phoneNumber}"))
                context.startActivity(intent)
            }

            binding.donorCardContent.setOnClickListener {
                onDonorSelected(donor)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDonorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(donors[position])
    }

    override fun getItemCount(): Int = donors.size

    fun updateDonors(newDonors: List<Donor>) {
        donors = newDonors
        notifyDataSetChanged()
    }
}
