package com.kemas.semantic_validation_app

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.kemas.semantic.ModelSelector
import com.kemas.semantic.SemanticValidator
import com.kemas.semantic_validation_app.databinding.ActivitySingleFormBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SingleForm : AppCompatActivity() {

    private lateinit var binding: ActivitySingleFormBinding
    private val validator = ValidatorContainer.validator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySingleFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnValidate.setOnClickListener {
            validateInput()
        }
    }

    private fun validateInput() {
        val input = binding.etInput.text.toString().trim()

        // Disable tombol agar tidak bisa double-click
        binding.btnValidate.isEnabled = false
        binding.btnValidate.alpha = 0.5f

        binding.tvResult.text = ""

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val result = validator.validateText(
                    input,
                    ModelSelector.GEMMA,
                    "cerita"
                )

                withContext(Dispatchers.Main) {

                    if (!result.valid) {
                        binding.tvResult.setTextColor(getColor(android.R.color.holo_red_dark))
                        binding.tvResult.text = "❌ Error:\n${result.message}"
                    } else {
                        binding.tvResult.setTextColor(getColor(android.R.color.holo_green_dark))
                        binding.tvResult.text = "✔ Valid:\n$input"
                    }

                    // Re-enable tombol
                    binding.btnValidate.isEnabled = true
                    binding.btnValidate.alpha = 1f
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    binding.tvResult.setTextColor(getColor(android.R.color.holo_red_dark))
                    binding.tvResult.text = "Error: ${e.message}"

                    // Enable lagi tombol
                    binding.btnValidate.isEnabled = true
                    binding.btnValidate.alpha = 1f
                }
            }
        }
    }
}

