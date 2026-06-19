package com.mantapp.app

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun packageName_isConfigured() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        assertEquals("com.mantapp.app", appContext.packageName)
    }
}
