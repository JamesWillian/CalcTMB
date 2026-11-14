package com.jammes.calctmb.data.review

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.util.Log
import androidx.core.net.toUri
import com.google.android.play.core.ktx.launchReview
import com.google.android.play.core.ktx.requestReview
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.jammes.calctmb.domain.repository.StoreReview
import kotlinx.coroutines.CancellationException

/**
 * A avaliação na Google Play pela In-App Review API: o cartão abre por cima do app e a pessoa
 * avalia sem sair dele.
 *
 * A API tem uma cota própria e, se o cartão já apareceu há pouco tempo (ou o app não foi
 * instalado pela loja), responde que deu certo sem mostrar nada. Por isso o Google desaconselha
 * ligar um botão direto a ela: o toque ficaria sem resposta. Aqui o botão "Avaliar" vale porque,
 * quando o cartão não aparece, a página do app na loja abre no lugar.
 *
 * https://developer.android.com/guide/playcore/in-app-review
 */
class GooglePlayStoreReview(
    context: Context,
    private val foregroundActivity: ForegroundActivity
) : StoreReview {

    private val manager: ReviewManager = ReviewManagerFactory.create(context)

    override suspend fun open() {
        val reviewInfo = requestCard()
        // A tela é lida só agora: a pessoa pode ter saído do app enquanto a loja respondia.
        val activity = foregroundActivity.activity ?: return
        val cardShown = reviewInfo != null && launchCard(activity, reviewInfo)
        if (!cardShown) openStorePage(activity)
    }

    private suspend fun requestCard(): ReviewInfo? = try {
        manager.requestReview()
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        // Um cartão que não abre não pode atrapalhar: fica só no log, e a página da loja entra.
        Log.w(TAG, "A Google Play não preparou o cartão de avaliação", e)
        null
    }

    /**
     * Mostra o cartão e diz se ele chegou a aparecer. A loja não conta, mas quando ela não
     * mostra nada o fluxo termina quase na hora; com o cartão na tela, a pessoa leva alguns
     * segundos para avaliar ou fechar.
     */
    private suspend fun launchCard(activity: Activity, reviewInfo: ReviewInfo): Boolean = try {
        val start = SystemClock.elapsedRealtime()
        manager.launchReview(activity, reviewInfo)
        val elapsed = SystemClock.elapsedRealtime() - start
        Log.d(TAG, "O fluxo do cartão de avaliação terminou em $elapsed ms")
        elapsed >= MIN_CARD_ON_SCREEN_MS
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        Log.w(TAG, "A Google Play não abriu o cartão de avaliação", e)
        false
    }

    /** A página do app na Play Store; sem a Play Store no aparelho, a mesma página no navegador. */
    private fun openStorePage(activity: Activity) {
        if (activity.isFinishing || activity.isDestroyed) return
        val page = Intent(Intent.ACTION_VIEW, "$STORE_PAGE_URL${activity.packageName}".toUri())
        val opened = activity.tryStart(Intent(page).setPackage(PLAY_STORE_PACKAGE)) ||
            activity.tryStart(page)
        if (!opened) Log.w(TAG, "Nada no aparelho abre a página do app na loja")
    }

    private fun Activity.tryStart(intent: Intent): Boolean = try {
        startActivity(intent)
        true
    } catch (e: ActivityNotFoundException) {
        false
    }

    private companion object {
        const val TAG = "StoreReview"
        const val PLAY_STORE_PACKAGE = "com.android.vending"
        const val STORE_PAGE_URL = "https://play.google.com/store/apps/details?id="

        /**
         * Abaixo disso o cartão não chegou a aparecer: ninguém avalia nem o fecha tão rápido. Sem
         * cartão, a loja devolve um fluxo vazio que termina na hora (0 ms, medido num emulador
         * com o app instalado fora da loja, no debug e no release).
         */
        const val MIN_CARD_ON_SCREEN_MS = 1_000L
    }
}
