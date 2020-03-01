package org.btelman.controlsdk

import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import org.btelman.controlsdk.models.Component
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class IsolatedComponentTest {
    @Test
    fun ValidateInstantiate() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getTargetContext()
        val holder = DummyComponent.Companion.Builder("foo", 123).build()
        val component = Component.instantiate(appContext, holder) as DummyComponent
        Assert.assertEquals("foo", component.bundle?.getString(DummyComponent.ARG1_KEY))
        Assert.assertEquals(123, component.bundle?.getInt(DummyComponent.ARG2_KEY))
    }
}