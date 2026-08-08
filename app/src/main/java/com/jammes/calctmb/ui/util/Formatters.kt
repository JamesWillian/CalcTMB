package com.jammes.calctmb.ui.util

import java.text.DateFormat
import java.util.Date
import java.util.Locale

/** kcal são estimativas: casas decimais só poluiriam a leitura. */
fun formatKcal(value: Double): String =
    String.format(Locale.getDefault(), "%,.0f", value)

fun formatWeight(weightKg: Double): String =
    if (weightKg % 1.0 == 0.0) String.format(Locale.getDefault(), "%.0f", weightKg)
    else String.format(Locale.getDefault(), "%.1f", weightKg)

fun formatHeight(heightCm: Double): String =
    String.format(Locale.getDefault(), "%.0f", heightCm)

fun formatDate(millis: Long): String =
    DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault()).format(Date(millis))

fun formatDateTime(millis: Long): String =
    DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, Locale.getDefault())
        .format(Date(millis))
