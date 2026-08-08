package com.jammes.calctmb.domain.usecase

import com.jammes.calctmb.domain.repository.MeasurementRepository

class RenameMeasurementUseCase(
    private val repository: MeasurementRepository
) {
    suspend operator fun invoke(id: Long, name: String?) =
        repository.updateName(id, name?.trim()?.takeIf { it.isNotEmpty() })
}
