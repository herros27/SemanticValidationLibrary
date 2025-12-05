package com.kemas.semantic

data class Field(val label: String, val value: String)
fun SemanticValidator.validateBatch(fields: List<Field>, model: ModelSelector): Map<String, ResponseData> {
    val output = mutableMapOf<String, ResponseData>()

    for (field in fields) {
        val result = validateText(
            text = field.value,
            model = model,
            label = field.label
        )

        // Convert ke ValidationResult lokal
        output[field.label] = ResponseData(
            valid = result.valid,
            message = result.message
        )
    }



    return output
}
