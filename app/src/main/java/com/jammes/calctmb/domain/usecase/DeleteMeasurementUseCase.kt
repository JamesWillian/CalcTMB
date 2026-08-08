package com.jammes.calctmb.domain.usecase

import com.jammes.calctmb.domain.repository.MeasurementRepository

class DeleteMeasurementUseCase(
    private val repository: MeasurementRepository
) {
    suspend operator fun invoke(id: Long) = repository.delete(id)
}
