package com.jammes.calctmb.domain.usecase

import com.jammes.calctmb.domain.model.BmrResult
import com.jammes.calctmb.domain.model.BodyProfile
import com.jammes.calctmb.domain.model.Sex

/**
 * Calcula a TMB pela equação de Harris-Benedict revisada e deriva o gasto
 * calórico estimado para cada objetivo.
 */
class CalculateBmrUseCase {

    operator fun invoke(profile: BodyProfile): BmrResult {
        val bmr = when (profile.sex) {
            Sex.MALE ->
                88.362 + (13.397 * profile.weightKg) + (4.799 * profile.heightCm) - (5.677 * profile.age)

            Sex.FEMALE ->
                447.593 + (9.247 * profile.weightKg) + (3.098 * profile.heightCm) - (4.330 * profile.age)
        }

        return BmrResult(
            bmr = bmr,
            weightGain = bmr * WEIGHT_GAIN_FACTOR,
            hypertrophy = bmr * HYPERTROPHY_FACTOR,
            weightLoss = bmr * WEIGHT_LOSS_FACTOR
        )
    }

    private companion object {
        const val WEIGHT_GAIN_FACTOR = 1.4
        const val HYPERTROPHY_FACTOR = 1.2
        const val WEIGHT_LOSS_FACTOR = 0.8
    }
}
