package com.jammes.calctmb.domain.usecase

import com.jammes.calctmb.domain.model.Measurement
import com.jammes.calctmb.domain.repository.MeasurementRepository
import kotlinx.coroutines.flow.Flow

class ObserveMeasurementsUseCase(
    private val repository: MeasurementRepository
) {
    operator fun invoke(): Flow<List<Measurement>> = repository.observeAll()
}
