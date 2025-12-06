package com.kemas.semantic_validation_app

import com.kemas.semantic.SemanticValidator

object ValidatorContainer {
    val validator by lazy { SemanticValidator(BuildConfig.GEMINI_KEY) }
}