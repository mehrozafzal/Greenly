package greenely.greenely.splash.ui.navigation

import android.app.Activity
import android.content.Intent
import com.nhaarman.mockito_kotlin.*
import greenely.greenely.login.ui.LoginActivity
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

class LoginRouteHandlerTest {
    private lateinit var activity: Activity
    private lateinit var next: NavigationHandler
    private lateinit var handler: LoginRouteHandler

    @Before
    fun setUp() {
        activity = mock()
        next = mock()

        handler = LoginRouteHandler(activity)
        handler.next = next
    }

    @Test
    fun testNavigateTo() {
        // Given
        val route = SplashRoute.LOGIN

        // When
        handler.navigateTo(route)

        // Then
        val intentCaptor = argumentCaptor<Intent>()

        verify(next, never()).navigateTo(route)
        verify(activity).startActivity(intentCaptor.capture())

        assertThat(intentCaptor.firstValue.component)
                .isEqualTo(Intent(activity, LoginActivity::class.java).component)
    }

    @Test
    fun testUnhandled() {
        // Given
        val route = SplashRoute.REGISTER

        // When
        handler.navigateTo(route)

        // Then
        verify(next).navigateTo(route)
        verify(activity, never()).startActivity(any())
    }
}
