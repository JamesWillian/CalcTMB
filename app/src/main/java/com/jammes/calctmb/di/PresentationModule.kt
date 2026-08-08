package com.jammes.calctmb.di

import com.jammes.calctmb.ui.calculator.CalculatorViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val presentationModule = module {
    viewModel {
        CalculatorViewModel(
            validateBodyProfile = get(),
            calculateBmr = get(),
            saveMeasurement = get(),
            observeMeasurements = get(),
            deleteMeasurement = get(),
            renameMeasurement = get(),
            shouldShowIntro = get(),
            markIntroAsShown = get()
        )
    }
}
