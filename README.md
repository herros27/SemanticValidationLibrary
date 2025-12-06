# 📚 Semantic Validation Android Library

An Android library for performing **AI-powered text validation** using Gemini/Gemma models.
Supports **single-field validation** and **batch validation** for multiple inputs at once.

---

## ✨ Features

* ✔ Intelligent text validation using LLM
* ✔ Supports multiple models ( `GEMINI_FLASH`, `GEMINI_FLASH_LATEST`, `GEMINI_FLASH_LITE`, `GEMMA`)
* ✔ Single text validation
* ✔ Batch validation (validate multiple fields in one request)
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
import your.name.package.SemanticValidator

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
                "cerita"                  // Validation context or rules
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

Validate multiple fields at once using `validateBatch()`.

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
            // 🟩 One single batch validation call
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

## 🧪 Sample Output (Single Validation)

```
✔ Valid:
This is a story about a young boy...
```

or:

```
❌ Error:
Input does not match the expected story criteria.
```

---

## 🧪 Sample Output (Batch Validation)

```
• Name is invalid:
  Name cannot be empty.

• Age is invalid:
  Age must be a number.
```

When everything is correct:

```
All fields are valid
```

---

## 🤝 Contributing

Pull Requests are welcome for:

* Adding new model support
* Improving validation pipeline
* Adding more test coverage

---

## 📄 License

MIT License – free to use for personal or commercial projects.