package com.jammes.calctmb.data.repository

import com.jammes.calctmb.data.local.dao.MeasurementDao
import com.jammes.calctmb.data.mapper.toDomain
import com.jammes.calctmb.data.mapper.toEntity
import com.jammes.calctmb.domain.model.Measurement
import com.jammes.calctmb.domain.repository.MeasurementRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MeasurementRepositoryImpl(
    private val dao: MeasurementDao
) : MeasurementRepository {

    override fun observeAll(): Flow<List<Measurement>> =
        dao.observeAll().map { entities -> entities.map { it.toDomain() } }

    override suspend fun save(measurement: Measurement): Long =
        dao.insert(measurement.toEntity().copy(id = 0L))

    override suspend fun delete(id: Long) = dao.deleteById(id)

    override suspend fun updateName(id: Long, name: String?) = dao.updateName(id, name)
}
