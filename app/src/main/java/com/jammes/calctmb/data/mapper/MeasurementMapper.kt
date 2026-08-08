package com.jammes.calctmb.data.mapper

import com.jammes.calctmb.data.local.entity.MeasurementEntity
import com.jammes.calctmb.domain.model.BodyProfile
import com.jammes.calctmb.domain.model.Measurement
import com.jammes.calctmb.domain.model.Sex

fun MeasurementEntity.toDomain(): Measurement = Measurement(
    id = id,
    name = name,
    createdAt = createdAt,
    profile = BodyProfile(
        sex = runCatching { Sex.valueOf(sex) }.getOrDefault(Sex.MALE),
        age = age,
        weightKg = weightKg,
        heightCm = heightCm
    )
)

fun Measurement.toEntity(): MeasurementEntity = MeasurementEntity(
    id = id,
    name = name,
    createdAt = createdAt,
    sex = profile.sex.name,
    age = profile.age,
    weightKg = profile.weightKg,
    heightCm = profile.heightCm
)
