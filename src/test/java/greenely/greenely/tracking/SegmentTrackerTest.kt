@file:Suppress("KDocMissingDocumentation")

package greenely.greenely.tracking

import android.app.Activity
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.segment.analytics.Analytics
import com.segment.analytics.AnalyticsContext
import com.segment.analytics.Properties
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareOnlyThisForTest
import org.powermock.modules.junit4.PowerMockRunner

@RunWith(PowerMockRunner::class)
@PrepareOnlyThisForTest(Analytics::class)
class SegmentTrackerTest {
    private lateinit var tracker: SegmentTracker
    private lateinit var analytics: Analytics
    private lateinit var activity: Activity

    @Before
    fun setUp() {
        analytics = mock {
            on { analyticsContext } doReturn com.nhaarman.mockito_kotlin.mock<AnalyticsContext>()
        }
        activity = mock()

        PowerMockito.mockStatic(Analytics::class.java)
        PowerMockito.`when`(Analytics.with(activity)).thenReturn(analytics)

        tracker = SegmentTracker(activity)
    }

    @Test
    fun testTrackDefaultScreen() {
        // When
        tracker.trackScreen("Screen")

        // Then
        verify(analytics).screen("Screen")
    }

    @Test
    fun testTrackScreen() {
        // Given
        val screen = "Other screen"

        // When
        tracker.trackScreen(screen)

        // Then
        verify(analytics).screen(screen)
    }

    @Test
    fun testTrack() {
        // Given
        val properties = mock<Properties>()
        val event = mock<Tracker.Event> {
            on { name } doReturn "Event"
            on { this.properties } doReturn properties
        }

        // When
        tracker.track(event)

        // Then
        verify(analytics).track(event.name, event.properties)
    }
}

