package com.jammes.calctmb.di

import com.jammes.calctmb.domain.usecase.AcceptReviewInviteUseCase
import com.jammes.calctmb.domain.usecase.CalculateBmrUseCase
import com.jammes.calctmb.domain.usecase.DeclineReviewInviteUseCase
import com.jammes.calctmb.domain.usecase.DeleteMeasurementUseCase
import com.jammes.calctmb.domain.usecase.MarkIntroAsShownUseCase
import com.jammes.calctmb.domain.usecase.ObserveMeasurementsUseCase
import com.jammes.calctmb.domain.usecase.RenameMeasurementUseCase
import com.jammes.calctmb.domain.usecase.SaveMeasurementUseCase
import com.jammes.calctmb.domain.usecase.ShouldInviteToReviewUseCase
import com.jammes.calctmb.domain.usecase.ShouldShowIntroUseCase
import com.jammes.calctmb.domain.usecase.ValidateBodyProfileUseCase
import org.koin.dsl.module

val domainModule = module {
    factory { CalculateBmrUseCase() }
    factory { ValidateBodyProfileUseCase() }
    factory { SaveMeasurementUseCase(get(), get()) }
    factory { ObserveMeasurementsUseCase(get()) }
    factory { DeleteMeasurementUseCase(get()) }
    factory { RenameMeasurementUseCase(get()) }
    factory { ShouldShowIntroUseCase(get()) }
    factory { MarkIntroAsShownUseCase(get()) }
    factory { ShouldInviteToReviewUseCase(get(), get()) }
    factory { AcceptReviewInviteUseCase(get(), get()) }
    factory { DeclineReviewInviteUseCase(get(), get()) }
}
