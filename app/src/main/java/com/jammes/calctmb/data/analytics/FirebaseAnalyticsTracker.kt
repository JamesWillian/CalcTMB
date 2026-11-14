package com.jammes.calctmb.data.analytics

import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import com.google.firebase.analytics.logEvent
import com.jammes.calctmb.domain.analytics.AnalyticsEvent
import com.jammes.calctmb.domain.analytics.AnalyticsTracker

/**
 * Envia os eventos ao Google Analytics, que os junta e manda em lotes. No debug, a
 * coleta fica desativada pelo manifesto e nada sai do aparelho.
 */
class FirebaseAnalyticsTracker : AnalyticsTracker {

    override fun track(event: AnalyticsEvent) {
        Firebase.analytics.logEvent(event.name) {
            event.params.forEach { (key, value) -> param(key, value) }
        }
    }
}
