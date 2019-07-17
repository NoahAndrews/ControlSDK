package org.btelman.controlsdk.streaming
import android.os.Bundle
import androidx.test.runner.AndroidJUnit4
import org.btelman.controlsdk.streaming.audio.processors.BaseAudioProcessor
import org.btelman.controlsdk.streaming.factories.AudioProcessorFactory
import org.btelman.controlsdk.streaming.factories.VideoProcessorFactory
import org.btelman.controlsdk.streaming.models.StreamInfo
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
@RunWith(AndroidJUnit4::class)
class AudioProcessorFactoryAndroidTest {
    @Test
    fun findProcessorTest() {
        //Assert.assertTrue(AudioProcessorFactory.findProcessor(Bundle()) is ) TODO

        val streamInfo = StreamInfo("http://example.com:3000") //testing default parameters. Should use either Camera1SurfaceTextureComponent or Camera2SurfaceTextureComponent
        //test custom class
        val bundle = Bundle()
        streamInfo.addToExistingBundle(bundle)
        AudioProcessorFactory.putClassInBundle(MockAudioProcessor::class.java, bundle)
        Assert.assertTrue(AudioProcessorFactory.findProcessor(bundle) is MockAudioProcessor)
    }

    @Test
    fun testAddClazzToBundle(){
        val bundle = Bundle()
        val clazz = MockAudioProcessor::class.java
        AudioProcessorFactory.putClassInBundle(clazz, bundle)
        Assert.assertEquals(clazz, VideoProcessorFactory.getClassFromBundle(bundle))
    }

    class MockAudioProcessor : BaseAudioProcessor() {
        override fun processAudioByteArray(data: ByteArray) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }
}
