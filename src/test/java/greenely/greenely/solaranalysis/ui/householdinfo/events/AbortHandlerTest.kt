package greenely.greenely.solaranalysis.ui.householdinfo.events

import android.app.Activity
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.verify
import org.junit.Before
import org.junit.Test

class AbortHandlerTest {
    private lateinit var activity: Activity
    private lateinit var next: EventHandler
    private lateinit var handler: AbortHandler

    @Before
    fun setUp() {
        activity = mock()
        next = mock()
        handler = AbortHandler(activity, next)
    }

    @Test
    fun testNext() {
        // Given
        val event: Event = mock()

        // When
        handler.handleEvent(event)

        // Then
        verify(next).handleEvent(event)
    }
}