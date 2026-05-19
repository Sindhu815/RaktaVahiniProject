package com.healthcare.raktavahini.ui.activities

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.healthcare.raktavahini.data.model.Donor
import com.healthcare.raktavahini.databinding.ActivitySearchDonorBinding
import com.healthcare.raktavahini.ui.adapters.DonorAdapter
import com.healthcare.raktavahini.ui.viewmodel.DonorViewModel
import com.healthcare.raktavahini.utils.Constants

class SearchDonorActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchDonorBinding
    private val viewModel: DonorViewModel by viewModels()
    private lateinit var adapter: DonorAdapter
    private var activeSearchSource: LiveData<List<Donor>>? = null
    private var latestResults: List<Donor> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchDonorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupBloodGroupSpinner()
        setupSearchButton()
    }

    private fun setupRecyclerView() {
        adapter = DonorAdapter(emptyList(), ::showDonorDetails)
        binding.rvDonors.layoutManager = LinearLayoutManager(this)
        binding.rvDonors.adapter = adapter
    }

    private fun setupBloodGroupSpinner() {
        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, Constants.BLOOD_GROUPS)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerBloodGroup.adapter = arrayAdapter

        val urgencyAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, Constants.URGENCY_LEVELS)
        urgencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerUrgency.adapter = urgencyAdapter
    }

    private fun setupSearchButton() {
        binding.btnSearch.setOnClickListener {
            searchDonors()
        }

        binding.btnWhatsappRequest.setOnClickListener {
            shareEmergencyRequest(preferWhatsapp = true)
        }

        binding.btnShareRequest.setOnClickListener {
            shareEmergencyRequest(preferWhatsapp = false)
        }

        binding.btnCompatibility.setOnClickListener {
            showCompatibilityGuide()
        }
    }

    private fun searchDonors() {
        activeSearchSource?.removeObservers(this)

        val bloodGroup = binding.spinnerBloodGroup.selectedItem.toString()
        val locationQuery = binding.etLocationQuery.text.toString().trim()
        activeSearchSource = viewModel.emergencySearch(bloodGroup, locationQuery)

        activeSearchSource?.observe(this) { donors ->
            val visibleDonors = donors
                .filter { it.isEligible() }
                .sortedBy { it.lastDonationDate }
            updateUI(visibleDonors)
        }
    }

    private fun updateUI(donors: List<Donor>) {
        latestResults = donors
        adapter.updateDonors(donors)
        val hasResults = donors.isNotEmpty()
        binding.rvDonors.visibility = if (hasResults) View.VISIBLE else View.GONE
        binding.tvNoResults.visibility = if (hasResults) View.GONE else View.VISIBLE
        binding.tvResultSummary.text = if (hasResults) {
            "${donors.size} eligible donor(s) ready to contact"
        } else {
            "No currently eligible donors found for this search"
        }
    }

    private fun shareEmergencyRequest(preferWhatsapp: Boolean) {
        val message = buildEmergencyMessage()
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, message)
            if (preferWhatsapp) {
                setPackage("com.whatsapp")
            }
        }

        try {
            startActivity(if (preferWhatsapp) intent else Intent.createChooser(intent, "Share emergency request"))
        } catch (_: ActivityNotFoundException) {
            val fallback = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, message)
            }
            startActivity(Intent.createChooser(fallback, "Share emergency request"))
        }
    }

    private fun buildEmergencyMessage(): String {
        val bloodGroup = binding.spinnerBloodGroup.selectedItem.toString()
        val location = binding.etLocationQuery.text.toString().trim().ifBlank { "nearby area" }
        val requester = binding.etRequester.text.toString().trim().ifBlank { "Rakta-Vahini request" }
        val urgency = binding.spinnerUrgency.selectedItem.toString()
        val compatibleInfo = Constants.BLOOD_COMPATIBILITY[bloodGroup].orEmpty()

        return """
            Blood donor needed
            Requester: $requester
            Blood group: $bloodGroup
            Location: $location
            Urgency: $urgency
            Eligible donors found in app: ${latestResults.size}
            $compatibleInfo
            
            Please respond only if available and medically eligible.
        """.trimIndent()
    }

    private fun showCompatibilityGuide() {
        val bloodGroup = binding.spinnerBloodGroup.selectedItem.toString()
        val guide = Constants.BLOOD_COMPATIBILITY.entries.joinToString("\n\n") { (group, info) ->
            "$group: $info"
        }

        AlertDialog.Builder(this)
            .setTitle("$bloodGroup compatibility")
            .setMessage("${Constants.BLOOD_COMPATIBILITY[bloodGroup]}\n\nAll groups:\n\n$guide")
            .setPositiveButton("Close", null)
            .show()
    }

    private fun showDonorDetails(donor: Donor) {
        val maskedPhone = maskPhone(donor.phoneNumber)
        val details = """
            Blood group: ${donor.bloodGroup}
            Location: ${donor.location}
            Phone: $maskedPhone
            Status: ${if (donor.isEligible()) "Eligible now" else "Not eligible"}
            
            Phone number is hidden on public results. Use a contact action only when there is a genuine requirement.
        """.trimIndent()

        AlertDialog.Builder(this)
            .setTitle(donor.name)
            .setMessage(details)
            .setPositiveButton("Call") { _, _ -> callDonor(donor.phoneNumber) }
            .setNegativeButton("WhatsApp") { _, _ -> whatsappDonor(donor) }
            .setNeutralButton("Close", null)
            .show()
    }

    private fun maskPhone(phoneNumber: String): String {
        return if (phoneNumber.length <= 4) {
            "Hidden"
        } else {
            "${phoneNumber.take(2)}******${phoneNumber.takeLast(2)}"
        }
    }

    private fun callDonor(phoneNumber: String) {
        startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber")))
    }

    private fun whatsappDonor(donor: Donor) {
        val message = Uri.encode(
            "Hello ${donor.name}, this is an emergency blood request from Rakta-Vahini for ${donor.bloodGroup} near ${binding.etLocationQuery.text.toString().trim().ifBlank { donor.location }}. Are you available to donate?"
        )
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/${donor.phoneNumber}?text=$message"))
        try {
            startActivity(intent)
        } catch (_: ActivityNotFoundException) {
            shareEmergencyRequest(preferWhatsapp = false)
        }
    }
}
