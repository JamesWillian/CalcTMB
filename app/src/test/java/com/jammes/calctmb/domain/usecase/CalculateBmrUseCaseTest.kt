package com.jammes.calctmb.domain.usecase

import com.jammes.calctmb.domain.model.BodyProfile
import com.jammes.calctmb.domain.model.Sex
import org.junit.Assert.assertEquals
import org.junit.Test

class CalculateBmrUseCaseTest {

    private val calculateBmr = CalculateBmrUseCase()

    @Test
    fun `calcula TMB masculina pela formula de Harris-Benedict revisada`() {
        val profile = BodyProfile(sex = Sex.MALE, age = 30, weightKg = 80.0, heightCm = 180.0)

        val expected = 88.362 + (13.397 * 80.0) + (4.799 * 180.0) - (5.677 * 30)

        assertEquals(expected, calculateBmr(profile).bmr, TOLERANCE)
    }

    @Test
    fun `calcula TMB feminina pela formula de Harris-Benedict revisada`() {
        val profile = BodyProfile(sex = Sex.FEMALE, age = 30, weightKg = 65.0, heightCm = 165.0)

        val expected = 447.593 + (9.247 * 65.0) + (3.098 * 165.0) - (4.330 * 30)

        assertEquals(expected, calculateBmr(profile).bmr, TOLERANCE)
    }

    @Test
    fun `deriva as metas caloricas a partir da TMB`() {
        val profile = BodyProfile(sex = Sex.MALE, age = 30, weightKg = 80.0, heightCm = 180.0)

        val result = calculateBmr(profile)

        assertEquals(result.bmr * 1.4, result.weightGain, TOLERANCE)
        assertEquals(result.bmr * 1.2, result.hypertrophy, TOLERANCE)
        assertEquals(result.bmr * 0.8, result.weightLoss, TOLERANCE)
    }

    @Test
    fun `mesmo perfil produz sempre o mesmo resultado`() {
        val profile = BodyProfile(sex = Sex.FEMALE, age = 41, weightKg = 72.4, heightCm = 168.0)

        assertEquals(calculateBmr(profile), calculateBmr(profile))
    }

    private companion object {
        const val TOLERANCE = 0.0001
    }
}
