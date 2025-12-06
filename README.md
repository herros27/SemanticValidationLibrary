# 📚 Semantic Validation Android Library

An Android library for performing **AI-powered text validation** using Gemini/Gemma models.
Supports **single-field validation**, **batch validation**, and **Jetpack Compose UI usage**.

---

## ✨ Features

* ✔ Intelligent text validation using LLM
* ✔ Supports multiple models (`GEMINI_FLASH`, `GEMINI_FLASH_LATEST`, `GEMINI_FLASH_LITE`, `GEMMA`)
* ✔ Single text validation
* ✔ Batch validation (validate multiple fields in one request)
* ✔ Jetpack Compose support
* ✔ Simple & developer-friendly API

---

## 📦 Installation

Add the dependency to your **module-level `build.gradle`**:

```gradle
dependencies {
    implementation("io.github.herros27:semantic-library:1.0.0")
}
```

---

## ⚙️ Setup

### 1. Add API Key to `local.properties`

```
GEMINI_KEY=YOUR_API_KEY
```

### 2. Expose the key in `BuildConfig`

`app/build.gradle`:

```gradle
android {
    defaultConfig {
        buildConfigField(
            "String",
            "GEMINI_KEY",
            "\"${project.properties['GEMINI_KEY']}\""
        )
    }
}
```

---

## 🧩 Create a Validator Singleton

Create `ValidatorContainer.kt`:

```kotlin
import com.kemas.semantic.SemanticValidator

object ValidatorContainer {
    val validator by lazy { SemanticValidator(BuildConfig.GEMINI_KEY) }
}
```

Use it anywhere:

```kotlin
private val validator = ValidatorContainer.validator
```

---

# ✅ Usage Examples

---

# 1️⃣ Single Text Validation

Use `validateText()` to validate a single string.

```kotlin
import com.kemas.semantic.SemanticValidator

val validator = ValidatorContainer.validator

private fun validateInput() {

    val input = binding.etInput.text.toString().trim()

    binding.btnValidate.isEnabled = false
    binding.btnValidate.alpha = 0.5f
    binding.tvResult.text = ""

    lifecycleScope.launch(Dispatchers.IO) {
        try {
            val result = validator.validateText(
                input,
                ModelSelector.GEMMA,     // Select validation model
                "cerita"                 // Validation rules/context
            )

            withContext(Dispatchers.Main) {
                if (!result.valid) {
                    binding.tvResult.setTextColor(getColor(android.R.color.holo_red_dark))
                    binding.tvResult.text = "❌ Error:\n${result.message}"
                } else {
                    binding.tvResult.setTextColor(getColor(android.R.color.holo_green_dark))
                    binding.tvResult.text = "✔ Valid:\n$input"
                }

                binding.btnValidate.isEnabled = true
                binding.btnValidate.alpha = 1f
            }

        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                binding.tvResult.setTextColor(getColor(android.R.color.holo_red_dark))
                binding.tvResult.text = "Error: ${e.message}"
                binding.btnValidate.isEnabled = true
                binding.btnValidate.alpha = 1f
            }
        }
    }
}
```

---

# 2️⃣ Batch Validation (Multiple Fields)

Validate multiple fields in a single call using `validateBatch()`.

```kotlin
import com.kemas.semantic.validateBatch

val validator = ValidatorContainer.validator

private fun submitBiodata() {
    val fields = listOf(
        Field("Name", binding.etNama.text.toString().trim()),
        Field("Age", binding.etUmur.text.toString().trim()),
        Field("Address", binding.etAlamat.text.toString().trim())
    )

    binding.progressBar.visibility = View.VISIBLE
    binding.tvResult.text = ""

    lifecycleScope.launch(Dispatchers.IO) {
        try {
            val results = validator.validateBatch(fields, ModelSelector.GEMINI_FLASH)

            withContext(Dispatchers.Main) {
                binding.progressBar.visibility = View.GONE

                val errors = results.entries
                    .filter { !it.value.valid }
                    .map { entry ->
                        "• ${entry.key} is invalid:\n  ${entry.value.message}"
                    }

                if (errors.isNotEmpty()) {
                    showError(errors.joinToString("\n\n"))
                    return@withContext
                }

                binding.tvResult.setTextColor(getColor(android.R.color.holo_green_dark))
                binding.tvResult.text = "All fields are valid"

                Log.d("VALIDATION", "Success: $results")
            }

        } catch (e: Exception) {
            Log.e("VALIDATION", "Validation Error: ${e.message}")
            withContext(Dispatchers.Main) {
                binding.progressBar.visibility = View.GONE
                showError("ERROR: ${e.message}")
            }
        }
    }
}
```

---

# 🌟 3️⃣ Jetpack Compose Example

Below is an example implementation using **Jetpack Compose** with coroutine handling, state management, and Card status UI.

```kotlin
@Composable
fun semanticValidationScreen(modifier: Modifier = Modifier) {

    var inputText by remember { mutableStateOf("") }
    var resultMessage by remember { mutableStateOf("No result yet") }
    var isValid by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    // Validator instance (Recommended to store in ViewModel)
    val validator = ValidatorContainer.validator

    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "Semantic Validator (Compose Example)",
            style = MaterialTheme.typography.headlineMedium
        )

        TextField(
            value = inputText,
            onValueChange = { inputText = it },
            label = { Text("Enter your text") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                enabled = !isLoading,
                onClick = {
                    scope.launch {
                        isLoading = true

                        try {
                            val response = withContext(Dispatchers.IO) {
                                validator.validateText(
                                    text = inputText,
                                    model = ModelSelector.GEMINI_FLASH,
                                    label = "alamat"
                                )
                            }

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
                    Text("Validate Now")
                }
            }
        }

        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (isValid)
                    MaterialTheme.colorScheme.primaryContainer
                else
                    MaterialTheme.colorScheme.errorContainer
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = resultMessage,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}
```

---

## 🧪 Sample Output (Single Validation)

```
✔ Valid:
This is a story about a young boy...
```

---

## 🧪 Sample Output (Batch Validation)

```
• Name is invalid:
  Name cannot be empty.

• Age is invalid:
  Age must be a number.
```

---

## 🤝 Contributing

Pull Requests are welcome for:

* Adding new model support
* Improving validation pipeline
* Adding more test coverage

---

## 📄 License

MIT License — free to use for personal or commercial projects.