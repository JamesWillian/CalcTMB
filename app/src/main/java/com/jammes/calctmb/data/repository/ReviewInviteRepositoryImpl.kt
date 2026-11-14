package com.jammes.calctmb.data.repository

import android.content.SharedPreferences
import androidx.core.content.edit
import com.jammes.calctmb.domain.model.ReviewInviteHistory
import com.jammes.calctmb.domain.repository.ReviewInviteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ReviewInviteRepositoryImpl(
    private val preferences: SharedPreferences
) : ReviewInviteRepository {

    override suspend fun history(): ReviewInviteHistory = withContext(Dispatchers.IO) {
        ReviewInviteHistory(
            accepted = preferences.getBoolean(KEY_ACCEPTED, false),
            declineCount = preferences.getInt(KEY_DECLINE_COUNT, 0),
            lastDeclinedAt = if (preferences.contains(KEY_LAST_DECLINED_AT)) {
                preferences.getLong(KEY_LAST_DECLINED_AT, 0L)
            } else {
                null
            }
        )
    }

    override suspend fun markAccepted() = withContext(Dispatchers.IO) {
        preferences.edit { putBoolean(KEY_ACCEPTED, true) }
    }

    override suspend fun markDeclined(atMillis: Long) = withContext(Dispatchers.IO) {
        val declineCount = preferences.getInt(KEY_DECLINE_COUNT, 0)
        preferences.edit {
            putInt(KEY_DECLINE_COUNT, declineCount + 1)
            putLong(KEY_LAST_DECLINED_AT, atMillis)
        }
    }

    private companion object {
        const val KEY_ACCEPTED = "review_invite_accepted"
        const val KEY_DECLINE_COUNT = "review_invite_decline_count"
        const val KEY_LAST_DECLINED_AT = "review_invite_last_declined_at"
    }
}
