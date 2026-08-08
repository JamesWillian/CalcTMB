package com.jammes.calctmb.domain.util

/**
 * Abstrai o relógio do sistema para manter os casos de uso testáveis.
 */
fun interface TimeProvider {
    fun currentTimeMillis(): Long
}
