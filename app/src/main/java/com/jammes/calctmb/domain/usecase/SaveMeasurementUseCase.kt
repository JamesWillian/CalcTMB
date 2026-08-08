package com.jammes.calctmb.domain.usecase

import com.jammes.calctmb.domain.model.BodyProfile
import com.jammes.calctmb.domain.model.Measurement
import com.jammes.calctmb.domain.repository.MeasurementRepository
import com.jammes.calctmb.domain.util.TimeProvider

/**
 * Guarda o cálculo com a data do momento em que foi salvo. O nome é opcional:
 * em branco vira `null`.
 */
class SaveMeasurementUseCase(
    private val repository: MeasurementRepository,
    private val timeProvider: TimeProvider
) {

    suspend operator fun invoke(profile: BodyProfile, name: String?): Long =
        repository.save(
            Measurement(
                name = name?.trim()?.takeIf { it.isNotEmpty() },
                createdAt = timeProvider.currentTimeMillis(),
                profile = profile
            )
        )
}
