package com.jammes.calctmb.domain.usecase

import com.jammes.calctmb.domain.model.BodyProfileField
import com.jammes.calctmb.domain.model.BodyProfileInput
import com.jammes.calctmb.domain.model.Sex
import com.jammes.calctmb.domain.model.ValidationResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidateBodyProfileUseCaseTest {

    private val validate = ValidateBodyProfileUseCase()

    @Test
    fun `converte entrada valida em perfil`() {
        val result = validate(input(age = "30", weight = "80.5", height = "180"))

        assertTrue(result is ValidationResult.Valid)
        val profile = (result as ValidationResult.Valid).profile
        assertEquals(30, profile.age)
        assertEquals(80.5, profile.weightKg, 0.0001)
        assertEquals(180.0, profile.heightCm, 0.0001)
        assertEquals(Sex.MALE, profile.sex)
    }

    @Test
    fun `aceita virgula como separador decimal`() {
        val result = validate(input(age = "30", weight = "80,5", height = "180"))

        assertTrue(result is ValidationResult.Valid)
        assertEquals(80.5, (result as ValidationResult.Valid).profile.weightKg, 0.0001)
    }

    @Test
    fun `campos em branco sao invalidos`() {
        val result = validate(input(age = "", weight = "", height = ""))

        assertEquals(
            setOf(BodyProfileField.AGE, BodyProfileField.WEIGHT, BodyProfileField.HEIGHT),
            (result as ValidationResult.Invalid).invalidFields
        )
    }

    @Test
    fun `aponta apenas o campo fora do limite`() {
        val result = validate(input(age = "150", weight = "80", height = "180"))

        assertEquals(
            setOf(BodyProfileField.AGE),
            (result as ValidationResult.Invalid).invalidFields
        )
    }

    @Test
    fun `zero esta fora do limite em todos os campos`() {
        val result = validate(input(age = "0", weight = "0", height = "0"))

        assertEquals(
            setOf(BodyProfileField.AGE, BodyProfileField.WEIGHT, BodyProfileField.HEIGHT),
            (result as ValidationResult.Invalid).invalidFields
        )
    }

    @Test
    fun `texto nao numerico e invalido`() {
        val result = validate(input(age = "abc", weight = "80", height = "180"))

        assertEquals(
            setOf(BodyProfileField.AGE),
            (result as ValidationResult.Invalid).invalidFields
        )
    }

    private fun input(age: String, weight: String, height: String) = BodyProfileInput(
        sex = Sex.MALE,
        age = age,
        weightKg = weight,
        heightCm = height
    )
}
