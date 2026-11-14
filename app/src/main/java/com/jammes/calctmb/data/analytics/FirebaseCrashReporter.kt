package com.jammes.calctmb.data.analytics

import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import com.jammes.calctmb.domain.analytics.CrashReporter

/**
 * Registra no Crashlytics, como não fatais, as falhas que o app tratou. No debug, a
 * coleta fica desligada pelo manifesto.
 */
class FirebaseCrashReporter : CrashReporter {

    override fun report(error: Throwable) {
        Firebase.crashlytics.recordException(error)
    }
}
