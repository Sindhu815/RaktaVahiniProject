package com.healthcare.raktavahini.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.healthcare.raktavahini.databinding.ActivityMainBinding
import com.healthcare.raktavahini.ui.viewmodel.DonorViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: DonorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.cardRegister.setOnClickListener {
            startActivity(Intent(this, RegisterDonorActivity::class.java))
        }

        binding.cardSearch.setOnClickListener {
            startActivity(Intent(this, SearchDonorActivity::class.java))
        }

        binding.cardDonationLog.setOnClickListener {
            startActivity(Intent(this, DonationLogActivity::class.java))
        }

        viewModel.allDonors.observe(this) { donors ->
            binding.tvTotalDonors.text = "${donors.size}\nDonors"
            binding.tvEligibleNow.text = "${donors.count { it.isEligible() }}\nEligible"
            binding.tvAreas.text = "${donors.map { it.location.lowercase().trim() }.distinct().size}\nAreas"
        }
    }
}
