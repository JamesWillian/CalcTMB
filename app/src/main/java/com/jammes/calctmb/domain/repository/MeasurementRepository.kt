package com.jammes.calctmb.domain.repository

import com.jammes.calctmb.domain.model.Measurement
import kotlinx.coroutines.flow.Flow

interface MeasurementRepository {

    /** Cálculos salvos, do mais recente para o mais antigo. */
    fun observeAll(): Flow<List<Measurement>>

    /** Salva um novo cálculo e devolve o id gerado. */
    suspend fun save(measurement: Measurement): Long

    suspend fun delete(id: Long)

    suspend fun updateName(id: Long, name: String?)
}
