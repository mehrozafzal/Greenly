package greenely.greenely.registration.ui.events

import android.view.View
import android.view.inputmethod.InputMethodManager
import com.nhaarman.mockito_kotlin.*
import greenely.greenely.registration.ui.RegistrationActivity
import org.junit.Before
import org.junit.Test

class HideKeyboardHandlerTest {
    private lateinit var activity: RegistrationActivity
    private lateinit var next: EventHandler
    private lateinit var inputManager: InputMethodManager
    private lateinit var handler: HideKeyboardHandler

    @Before
    fun setUp() {
        inputManager = mock()
        activity = mock {
            on { currentFocus } doReturn mock<View>()
            on { getSystemService(any()) } doReturn inputManager
        }
        next = mock()

        handler = HideKeyboardHandler(activity, next)
    }

    @Test
    fun testHandleEvent() {
        // Given
        val event = Event.HideKeyboard

        // When
        handler.handleEvent(event)

        // Then
        verify(inputManager).hideSoftInputFromWindow(null, 0)
        verify(next, never()).handleEvent(any())
    }

    @Test
    fun testNext() {
        // Given
        val event = Event.Exit

        // When
        handler.handleEvent(event)

        // Then
        verify(next).handleEvent(event)
    }
}
