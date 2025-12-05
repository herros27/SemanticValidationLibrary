package com.kemas.semantic_validation_app

import com.kemas.semantic.SemanticValidator

object ValidatorContainer {
    val validator by lazy {
        SemanticValidator(apiKey = BuildConfig.GEMINI_KEY)
    }
}