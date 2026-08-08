package com.jammes.calctmb.domain.model

/**
 * Dados corporais usados no cálculo da TMB, já validados e convertidos.
 */
data class BodyProfile(
    val sex: Sex,
    val age: Int,
    val weightKg: Double,
    val heightCm: Double
)

/**
 * Limites aceitos para cada campo. Ficam no domínio para que a UI e a validação
 * usem exatamente a mesma regra.
 */
object BodyProfileLimits {
    val AGE_RANGE = 1..120
    val WEIGHT_RANGE = 1.0..500.0
    val HEIGHT_RANGE = 1.0..300.0
}
