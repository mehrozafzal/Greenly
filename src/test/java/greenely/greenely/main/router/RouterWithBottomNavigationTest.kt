package greenely.greenely.main.router

import com.google.android.material.bottomnavigation.BottomNavigationView
import android.view.MenuItem
import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import greenely.greenely.R
import org.junit.Before
import org.junit.Test

class RouterWithBottomNavigationTest {
    private lateinit var wrapped: Router
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var router: RouterWithBottomNavigation

    @Before
    fun setUp() {
        wrapped = mock()
        bottomNavigation = mock()

        router = RouterWithBottomNavigation(wrapped, bottomNavigation)
    }

    @Test
    fun testRouteTo() {
        // Given
        val route = Route.HOME

        // When
        router.routeTo(route)

        // Then
        verify(wrapped).routeTo(route)
        verify(bottomNavigation).selectedItemId = R.id.home
    }

    @Test
    fun testOnNavigationItemSelectedListener() {
        // Given
        val listenerCaptor = argumentCaptor<BottomNavigationView.OnNavigationItemSelectedListener>()
        verify(bottomNavigation).setOnNavigationItemSelectedListener(listenerCaptor.capture())
        val listener = listenerCaptor.lastValue

        val menuItem = mock<MenuItem> {
            on { itemId } doReturn R.id.home
        }

        // When
        listener.onNavigationItemSelected(menuItem)

        // Then
        verify(wrapped).routeTo(Route.HOME)
    }
}