package com.kemas.semantic_validation_app

import android.content.Intent
import android.os.Bundle
import android.util.Log // Import Log agar lebih rapi di Logcat
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope // Import ini
import com.kemas.semantic.Field
import com.kemas.semantic.ModelSelector
import kotlinx.coroutines.Dispatchers // Import ini
import kotlinx.coroutines.launch // Import ini
import com.kemas.semantic.SemanticValidator
import com.kemas.semantic.validateBatch
import com.kemas.semantic_validation_app.databinding.ActivityMainBinding
import kotlinx.coroutines.withContext

class  MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val TAG = "RustSemantic"

    private val validator by lazy {
        SemanticValidator(BuildConfig.GEMINI_KEY)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.btnSubmit.setOnClickListener { submitBiodata() }
        binding.btnPindah.setOnClickListener {
            val intent = Intent(this, SingleForm::class.java)
            startActivity(intent)
        }
        binding.btnPindahCompose.setOnClickListener {
            val intent = Intent(this, SemanticFormCompose::class.java)
            startActivity(intent)
        }
    }

    private fun submitBiodata() {
        val fields = listOf(
            Field("Nama", binding.etNama.text.toString().trim()),
            Field("Umur", binding.etUmur.text.toString().trim()),
            Field("Alamat", binding.etAlamat.text.toString().trim())
        )



        binding.progressBar.visibility = View.VISIBLE
        binding.tvResult.text = ""

        lifecycleScope.launch(Dispatchers.IO) {
            try {

                // 🟩 Hanya satu kali panggilan batch
                val results = validator.validateBatch(fields, ModelSelector.GEMINI_FLASH)

                withContext(Dispatchers.Main) {
                    binding.progressBar.visibility = View.GONE

                    // Ambil semua error
                    val errors = results.entries
                        .filter { !it.value.valid }
                        .map { entry ->
                            "• ${entry.key} tidak valid:\n  ${entry.value.message}"
                        }

                    // Jika ada error
                    if (errors.isNotEmpty()) {
                        showError(errors.joinToString("\n\n"))

                        return@withContext
                    }

                    // Semua valid
                    binding.tvResult.setTextColor(getColor(android.R.color.holo_green_dark))
                    binding.tvResult.text = "Semua data valid"

                    Log.d(TAG, "Semua validasi sukses: $results")
                }

            } catch (e: Exception) {
                Log.e(TAG, "Error Validasi: ${e.message}")
                withContext(Dispatchers.Main) {
                    binding.progressBar.visibility = View.GONE
                    showError("ERROR: ${e.message}")
                }
            }
        }
    }

    private fun showError(msg: String) {
        binding.tvResult.setTextColor(getColor(android.R.color.holo_red_dark))
        binding.tvResult.text = msg
    }

}