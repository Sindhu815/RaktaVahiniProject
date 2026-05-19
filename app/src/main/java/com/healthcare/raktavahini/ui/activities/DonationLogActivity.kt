package com.healthcare.raktavahini.ui.activities

import android.Manifest
import android.app.DatePickerDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.healthcare.raktavahini.R
import com.healthcare.raktavahini.data.model.DonationLog
import com.healthcare.raktavahini.databinding.ActivityDonationLogBinding
import com.healthcare.raktavahini.ui.adapters.DonationLogAdapter
import com.healthcare.raktavahini.ui.viewmodel.DonorViewModel
import com.healthcare.raktavahini.utils.Constants
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class DonationLogActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDonationLogBinding
    private val viewModel: DonorViewModel by viewModels()
    private lateinit var adapter: DonationLogAdapter
    private var selectedDate: Long = System.currentTimeMillis()
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            showThankYouNotification()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDonationLogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        createNotificationChannel()
        setupBloodGroupSpinner()
        setupDatePicker()
        setupHistoryList()
        setupSaveButton()
    }

    private fun setupBloodGroupSpinner() {
        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, Constants.BLOOD_GROUPS)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerBloodGroup.adapter = arrayAdapter
    }

    private fun setupDatePicker() {
        binding.btnSelectDate.text = dateFormat.format(Date(selectedDate))
        binding.btnSelectDate.setOnClickListener {
            val calendar = Calendar.getInstance().apply { timeInMillis = selectedDate }
            DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    selectedDate = Calendar.getInstance().apply {
                        set(year, month, dayOfMonth, 0, 0, 0)
                        set(Calendar.MILLISECOND, 0)
                    }.timeInMillis
                    binding.btnSelectDate.text = dateFormat.format(Date(selectedDate))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).apply {
                datePicker.maxDate = System.currentTimeMillis()
            }.show()
        }
    }

    private fun setupHistoryList() {
        adapter = DonationLogAdapter(emptyList())
        binding.rvDonationLogs.layoutManager = LinearLayoutManager(this)
        binding.rvDonationLogs.adapter = adapter

        viewModel.allDonationLogs.observe(this) { logs ->
            adapter.updateLogs(logs)
            binding.tvNoLogs.visibility = if (logs.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun setupSaveButton() {
        binding.btnSaveLog.setOnClickListener {
            val donorName = binding.etDonorName.text.toString().trim()
            val location = binding.etLocation.text.toString().trim()

            when {
                donorName.isEmpty() -> binding.tilDonorName.error = "Name required"
                location.isEmpty() -> binding.tilLocation.error = "Location required"
                else -> {
                    binding.tilDonorName.error = null
                    binding.tilLocation.error = null
                    val log = DonationLog(
                        donorName = donorName,
                        bloodGroup = binding.spinnerBloodGroup.selectedItem.toString(),
                        location = location,
                        donatedAt = selectedDate,
                        note = binding.etNote.text.toString().trim()
                    )
                    viewModel.insertDonationLog(log)
                    clearForm()
                    Toast.makeText(this, "Donation logged. Thank you!", Toast.LENGTH_SHORT).show()
                    notifyOrRequestPermission()
                }
            }
        }
    }

    private fun clearForm() {
        binding.etDonorName.text?.clear()
        binding.etLocation.text?.clear()
        binding.etNote.text?.clear()
        selectedDate = System.currentTimeMillis()
        binding.btnSelectDate.text = dateFormat.format(Date(selectedDate))
    }

    private fun notifyOrRequestPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        ) {
            showThankYouNotification()
        } else {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                THANK_YOU_CHANNEL_ID,
                "Donation Thanks",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }

    private fun showThankYouNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val notification = NotificationCompat.Builder(this, THANK_YOU_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_blood_drop)
            .setContentTitle("Thank you for donating")
            .setContentText("Your donation log was saved. You helped keep Rakta-Vahini ready.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(this).notify(THANK_YOU_NOTIFICATION_ID, notification)
    }

    companion object {
        private const val THANK_YOU_CHANNEL_ID = "donation_thanks"
        private const val THANK_YOU_NOTIFICATION_ID = 1001
    }
}
