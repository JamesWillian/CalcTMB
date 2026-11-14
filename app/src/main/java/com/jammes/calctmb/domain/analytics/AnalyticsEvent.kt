package com.jammes.calctmb.domain.analytics

import com.jammes.calctmb.domain.model.BodyProfileField

/**
 * Ações do usuário registradas no Analytics. Nenhum evento carrega dados corporais
 * (sexo, idade, peso, altura, TMB) nem o nome dos cálculos salvos: são dados de
 * saúde e não saem do aparelho.
 */
sealed class AnalyticsEvent(
    val name: String,
    val params: Map<String, String> = emptyMap()
) {
    data object BmrCalculated : AnalyticsEvent("bmr_calculated")

    /** Leva só quais campos estavam inválidos, nunca o que foi digitado. */
    data class InvalidForm(val fields: Set<BodyProfileField>) : AnalyticsEvent(
        name = "invalid_form",
        params = mapOf("fields" to fields.sorted().joinToString(",") { it.name.lowercase() })
    )

    data object FormCleared : AnalyticsEvent("form_cleared")

    data object MeasurementSaved : AnalyticsEvent("measurement_saved")

    data object MeasurementReused : AnalyticsEvent("measurement_reused")

    data object MeasurementRenamed : AnalyticsEvent("measurement_renamed")

    data object MeasurementDeleted : AnalyticsEvent("measurement_deleted")

    /** O usuário abriu a explicação sobre a TMB; a exibição automática inicial não conta. */
    data object AboutOpened : AnalyticsEvent("about_opened")

    /** O convite para avaliar o app na loja apareceu, depois de um cálculo salvo. */
    data object ReviewInviteShown : AnalyticsEvent("review_invite_shown")

    /** O usuário tocou em "Avaliar". A loja nunca conta se ele chegou a avaliar. */
    data object ReviewInviteAccepted : AnalyticsEvent("review_invite_accepted")

    /** O usuário deixou para depois: "Agora não", voltar ou toque fora do convite. */
    data object ReviewInviteDeclined : AnalyticsEvent("review_invite_declined")
}
