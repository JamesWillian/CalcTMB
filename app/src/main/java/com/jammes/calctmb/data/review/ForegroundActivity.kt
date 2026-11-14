package com.jammes.calctmb.data.review

import android.app.Activity
import android.app.Application
import android.os.Bundle
import java.lang.ref.WeakReference

/**
 * A Activity que está à vista. O cartão de avaliação da Google Play só abre por cima de uma
 * tela, e assim a loja chega nela sem a Activity passar pelo ViewModel nem pelo domínio. A
 * CalcTmbApplication registra no onCreate.
 *
 * Fica numa WeakReference: guardar a Activity em si a prenderia na memória depois de fechada.
 */
class ForegroundActivity : Application.ActivityLifecycleCallbacks {

    @Volatile
    private var current: WeakReference<Activity>? = null

    /** A tela à vista; null com o app em segundo plano. */
    val activity: Activity?
        get() = current?.get()

    override fun onActivityResumed(activity: Activity) {
        current = WeakReference(activity)
    }

    override fun onActivityPaused(activity: Activity) {
        // Só sai se for ela: na troca de telas, a próxima já pode ter entrado.
        if (current?.get() === activity) current = null
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) = Unit
    override fun onActivityStarted(activity: Activity) = Unit
    override fun onActivityStopped(activity: Activity) = Unit
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit
    override fun onActivityDestroyed(activity: Activity) = Unit
}
