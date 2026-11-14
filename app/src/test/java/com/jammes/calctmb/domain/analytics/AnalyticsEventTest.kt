package com.jammes.calctmb.domain.analytics

import com.jammes.calctmb.domain.model.BodyProfileField
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AnalyticsEventTest {

    /** Todos os eventos do app: um evento novo precisa entrar aqui para ser validado. */
    private val allEvents = listOf(
        AnalyticsEvent.BmrCalculated,
        AnalyticsEvent.InvalidForm(BodyProfileField.entries.toSet()),
        AnalyticsEvent.FormCleared,
        AnalyticsEvent.MeasurementSaved,
        AnalyticsEvent.MeasurementReused,
        AnalyticsEvent.MeasurementRenamed,
        AnalyticsEvent.MeasurementDeleted,
        AnalyticsEvent.AboutOpened,
        AnalyticsEvent.ReviewInviteShown,
        AnalyticsEvent.ReviewInviteAccepted,
        AnalyticsEvent.ReviewInviteDeclined
    )

    @Test
    fun `nomes e parametros seguem as regras do Google Analytics`() {
        allEvents.forEach { event ->
            assertTrue(event.name, VALID_NAME.matches(event.name))
            assertFalse(event.name, RESERVED_PREFIXES.any { event.name.startsWith(it) })
            event.params.forEach { (key, value) ->
                assertTrue(key, VALID_NAME.matches(key))
                assertTrue(value, value.length <= MAX_PARAM_VALUE_LENGTH)
            }
        }
    }

    @Test
    fun `nomes nao se repetem`() {
        assertEquals(allEvents.size, allEvents.map { it.name }.toSet().size)
    }

    @Test
    fun `formulario invalido leva so os nomes dos campos`() {
        val event = AnalyticsEvent.InvalidForm(setOf(BodyProfileField.HEIGHT, BodyProfileField.AGE))

        assertEquals(mapOf("fields" to "age,height"), event.params)
    }

    @Test
    fun `os demais eventos nao levam parametros`() {
        allEvents.filterNot { it is AnalyticsEvent.InvalidForm }
            .forEach { event -> assertTrue(event.name, event.params.isEmpty()) }
    }

    private companion object {
        // Até 40 caracteres, começando por letra, só com letras, números e "_".
        val VALID_NAME = Regex("^[a-zA-Z][a-zA-Z0-9_]{0,39}$")
        val RESERVED_PREFIXES = listOf("firebase_", "google_", "ga_")
        const val MAX_PARAM_VALUE_LENGTH = 100
    }
}
