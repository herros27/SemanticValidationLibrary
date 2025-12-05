package com.kemas.semantic_validation_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kemas.semantic.ModelSelector
import com.kemas.semantic.SemanticValidator
import com.kemas.semantic_validation_app.ui.theme.Semantic_validation_appTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SemanticFormCompose : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Semantic_validation_appTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SemanticValidationScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun SemanticValidationScreen(modifier: Modifier = Modifier) {
    // 1. State untuk UI
    var inputText by remember { mutableStateOf("") }
    var resultMessage by remember { mutableStateOf("Hasil belum ada") }
    var isValid by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    // Scope untuk menjalankan fungsi suspend/background
    val scope = rememberCoroutineScope()

    // 2. Instance Validator (Disarankan disimpan di ViewModel, tapi di sini oke untuk demo)
    val validator = remember {
        SemanticValidator(apiKey = BuildConfig.GEMINI_KEY)
    }

    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(text = "Rust Semantic Validator", style = MaterialTheme.typography.headlineMedium)

        TextField(
            value = inputText,
            onValueChange = { inputText = it },
            label = { Text("Masukkan kalimat") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ){
            Button(
                enabled = !isLoading,
                onClick = {
                    scope.launch {
                        isLoading = true

                        // 3. PANGGIL LIBRARY RUST ANDA DI SINI
                        // Gunakan Dispatchers.IO agar tidak memblokir UI thread (cegah ANR)
                        try {
                            val response = withContext(Dispatchers.IO) {
                                validator.validateText(
                                    text = inputText,
                                    model = ModelSelector.GEMINI_FLASH, // Sesuaikan enum generate
                                    label = "alamat"
                                )
                            }

                            // Update UI berdasarkan hasil Rust
                            isValid = response.valid
                            resultMessage = response.message
                        } catch (e: Exception) {
                            resultMessage = "Error: ${e.message}"
                        } finally {
                            isLoading = false
                        }
                    }
                }
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                } else {
                    Text("Validasi Sekarang")
                }
            }
        }


        // Tampilkan Hasil
        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (isValid) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.errorContainer
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            // Gunakan Modifier.padding di sini
            Text(
                text = resultMessage,
                modifier = Modifier.padding(16.dp) // <--- INI CARA YANG BENAR
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SemanticValidationScreenPreview() {
    Semantic_validation_appTheme {
        SemanticValidationScreen()
    }
}