package com.jammes.calctmb.domain.model

/**
 * Taxa de Metabolismo Basal e o gasto calórico estimado para cada objetivo,
 * todos em kcal/dia.
 */
data class BmrResult(
    val bmr: Double,
    val weightGain: Double,
    val hypertrophy: Double,
    val weightLoss: Double
)
