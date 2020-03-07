package greenely.greenely.splash.ui.navigation

import android.app.Activity
import android.content.Intent
import com.nhaarman.mockito_kotlin.*
import greenely.greenely.registration.ui.RegistrationActivity
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

class RegistrationRouteHandlerTest {
    private lateinit var activity: Activity
    private lateinit var next: NavigationHandler
    private lateinit var handler: RegistrationRouteHandler

    @Before
    fun setUp() {
        activity = mock()
        next = mock()

        handler = RegistrationRouteHandler(activity)
        handler.next = next
    }

    @Test
    fun testNavigateTo() {
        // Given
        val route = SplashRoute.REGISTER

        // When
        handler.navigateTo(route)

        // Then
        val intentCaptor = argumentCaptor<Intent>()
        verify(activity).startActivity(intentCaptor.capture())
        verify(next, never()).navigateTo(any())

        assertThat(intentCaptor.firstValue.component)
                .isEqualTo(Intent(activity, RegistrationActivity::class.java).component)
    }

    @Test
    fun testUnhandled() {
        // Given
        val route = SplashRoute.LOGIN

        // When
        handler.navigateTo(route)

        // Then
        verify(activity, never()).startActivity(any())
        verify(next).navigateTo(route)
    }
}
