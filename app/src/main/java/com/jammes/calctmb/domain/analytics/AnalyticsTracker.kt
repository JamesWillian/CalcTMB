package com.jammes.calctmb.domain.analytics

/**
 * Registra o uso do app. A implementação decide para onde os eventos vão, e os
 * testes usam uma versão em memória.
 */
fun interface AnalyticsTracker {
    fun track(event: AnalyticsEvent)
}
