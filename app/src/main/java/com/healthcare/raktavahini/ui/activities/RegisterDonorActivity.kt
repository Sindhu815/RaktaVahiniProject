package com.healthcare.raktavahini.ui.activities

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.healthcare.raktavahini.data.model.Donor
import com.healthcare.raktavahini.databinding.ActivityRegisterDonorBinding
import com.healthcare.raktavahini.ui.viewmodel.DonorViewModel
import com.healthcare.raktavahini.utils.Constants
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class RegisterDonorActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterDonorBinding
    private val viewModel: DonorViewModel by viewModels()
    private var selectedDate: Long = System.currentTimeMillis()
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterDonorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBloodGroupSpinner()
        setupDatePicker()
        setupRegisterButton()
    }

    private fun setupBloodGroupSpinner() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, Constants.BLOOD_GROUPS)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerBloodGroup.adapter = adapter
    }

    private fun setupDatePicker() {
        binding.btnSelectDate.text = dateFormat.format(Date(selectedDate))
        binding.btnSelectDate.setOnClickListener {
            val calendar = Calendar.getInstance().apply { timeInMillis = selectedDate }
            DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    val newDate = Calendar.getInstance().apply {
                        set(year, month, dayOfMonth, 0, 0, 0)
                        set(Calendar.MILLISECOND, 0)
                    }
                    selectedDate = newDate.timeInMillis
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

    private fun setupRegisterButton() {
        binding.btnRegister.setOnClickListener {
            val age = validateInput() ?: return@setOnClickListener
            val donor = Donor(
                name = binding.etName.text.toString().trim(),
                age = age,
                phoneNumber = binding.etPhone.text.toString().trim(),
                email = binding.etEmail.text.toString().trim(),
                location = binding.etLocation.text.toString().trim(),
                bloodGroup = binding.spinnerBloodGroup.selectedItem.toString(),
                lastDonationDate = selectedDate,
                isAvailable = binding.cbAvailable.isChecked
            )

            viewModel.insert(donor)
            Toast.makeText(this, "Donor registered successfully", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun validateInput(): Int? {
        clearErrors()

        val name = binding.etName.text.toString().trim()
        val ageText = binding.etAge.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val location = binding.etLocation.text.toString().trim()
        val age = ageText.toIntOrNull()

        return when {
            name.isEmpty() -> {
                binding.tilName.error = "Name required"
                null
            }
            age == null -> {
                binding.tilAge.error = "Valid age required"
                null
            }
            age !in Constants.MIN_AGE..Constants.MAX_AGE -> {
                binding.tilAge.error = "Age must be ${Constants.MIN_AGE}-${Constants.MAX_AGE}"
                null
            }
            phone.filter { it.isDigit() }.length < 10 -> {
                binding.tilPhone.error = "Valid phone required"
                null
            }
            location.isEmpty() -> {
                binding.tilLocation.error = "Location required"
                null
            }
            else -> age
        }
    }

    private fun clearErrors() {
        binding.tilName.error = null
        binding.tilAge.error = null
        binding.tilPhone.error = null
        binding.tilLocation.error = null
    }
}
