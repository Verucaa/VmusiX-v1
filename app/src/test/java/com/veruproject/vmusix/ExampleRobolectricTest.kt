package com.veruproject.vmusix

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.veruproject.vmusix.config.BrandConfig
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ExampleRobolectricTest {

    @Test
    fun testContextAndApplicationLoading() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        assertNotNull(context)
        assertEquals("Vmusix", BrandConfig.APP_NAME)
    }
}
