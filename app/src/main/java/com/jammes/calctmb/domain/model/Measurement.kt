package com.jammes.calctmb.domain.model

/**
 * Um cálculo salvo pelo usuário. O nome é opcional e só pode ser definido
 * no momento de salvar (ou depois, renomeando).
 */
data class Measurement(
    val id: Long = NEW_ID,
    val name: String? = null,
    val createdAt: Long,
    val profile: BodyProfile
) {
    companion object {
        const val NEW_ID = 0L
    }
}
