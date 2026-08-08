package com.jammes.calctmb.domain.usecase

import com.jammes.calctmb.domain.model.BodyProfile
import com.jammes.calctmb.domain.model.BodyProfileField
import com.jammes.calctmb.domain.model.BodyProfileInput
import com.jammes.calctmb.domain.model.BodyProfileLimits
import com.jammes.calctmb.domain.model.ValidationResult

/**
 * Converte o texto digitado em um [BodyProfile] válido ou devolve os campos
 * que precisam ser corrigidos.
 */
class ValidateBodyProfileUseCase {

    operator fun invoke(input: BodyProfileInput): ValidationResult {
        val age = input.age.trim().toIntOrNull()
            ?.takeIf { it in BodyProfileLimits.AGE_RANGE }
        val weight = input.weightKg.toDecimalOrNull()
            ?.takeIf { it in BodyProfileLimits.WEIGHT_RANGE }
        val height = input.heightCm.toDecimalOrNull()
            ?.takeIf { it in BodyProfileLimits.HEIGHT_RANGE }

        val invalidFields = buildSet {
            if (age == null) add(BodyProfileField.AGE)
            if (weight == null) add(BodyProfileField.WEIGHT)
            if (height == null) add(BodyProfileField.HEIGHT)
        }

        if (invalidFields.isNotEmpty()) return ValidationResult.Invalid(invalidFields)

        return ValidationResult.Valid(
            BodyProfile(
                sex = input.sex,
                age = age!!,
                weightKg = weight!!,
                heightCm = height!!
            )
        )
    }

    /** Aceita tanto `70.5` quanto `70,5`, já que o app é usado em pt/es/en. */
    private fun String.toDecimalOrNull(): Double? =
        trim().replace(',', '.').toDoubleOrNull()
}
