package com.jammes.calctmb.domain.model

/**
 * O que o usuário digitou, ainda em texto bruto.
 */
data class BodyProfileInput(
    val sex: Sex,
    val age: String,
    val weightKg: String,
    val heightCm: String
)

enum class BodyProfileField {
    AGE,
    WEIGHT,
    HEIGHT
}

sealed interface ValidationResult {
    data class Valid(val profile: BodyProfile) : ValidationResult
    data class Invalid(val invalidFields: Set<BodyProfileField>) : ValidationResult
}
