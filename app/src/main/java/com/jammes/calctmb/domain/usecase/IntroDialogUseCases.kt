package com.jammes.calctmb.domain.usecase

import com.jammes.calctmb.domain.repository.IntroRepository

class ShouldShowIntroUseCase(
    private val repository: IntroRepository
) {
    suspend operator fun invoke(): Boolean = !repository.wasIntroShown()
}

class MarkIntroAsShownUseCase(
    private val repository: IntroRepository
) {
    suspend operator fun invoke() = repository.markIntroAsShown()
}
