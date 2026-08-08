package com.jammes.calctmb.data.repository

import android.content.SharedPreferences
import androidx.core.content.edit
import com.jammes.calctmb.domain.repository.IntroRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class IntroRepositoryImpl(
    private val preferences: SharedPreferences
) : IntroRepository {

    override suspend fun wasIntroShown(): Boolean = withContext(Dispatchers.IO) {
        preferences.getBoolean(KEY_INTRO_SHOWN, false)
    }

    override suspend fun markIntroAsShown() = withContext(Dispatchers.IO) {
        preferences.edit { putBoolean(KEY_INTRO_SHOWN, true) }
    }

    private companion object {
        // Mesma chave usada na versão anterior, para quem já viu o diálogo não vê de novo.
        const val KEY_INTRO_SHOWN = "showAbout"
    }
}
