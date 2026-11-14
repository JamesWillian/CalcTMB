package com.jammes.calctmb.domain.analytics

/**
 * Registra falhas que o app tratou sem fechar. Os crashes de verdade o Crashlytics
 * já captura sozinho.
 */
fun interface CrashReporter {
    fun report(error: Throwable)
}
