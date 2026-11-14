package com.jammes.calctmb.domain.repository

/**
 * A avaliação do app na loja. O cartão de avaliação é da Google Play, e é ela quem decide se
 * ele aparece: pode não mostrar nada, sem avisar, e nunca conta se a pessoa avaliou.
 */
interface StoreReview {

    /**
     * Abre o cartão de avaliação por cima do app ou, quando a loja não o mostra, a página do
     * app na loja. Volta quando o cartão fecha ou a página abre.
     */
    suspend fun open()
}
