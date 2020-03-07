package greenely.greenely.main.router

import android.os.Parcel
import com.nhaarman.mockito_kotlin.*
import greenely.greenely.feed.ui.FeedFragment
import greenely.greenely.guidance.ui.CompeteFriendFragment
import greenely.greenely.history.HistoryFragment
import greenely.greenely.home.ui.HomeFragment
import greenely.greenely.main.ui.MainActivity
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(
        packageName = "greenely.greenely",
        sdk = [21]
)
class MainFragmentRouterTest {
    private lateinit var activity: MainActivity
    private lateinit var router: MainFragmentRouter
    private lateinit var transaction: androidx.fragment.app.FragmentTransaction

    @Before
    fun setUp() {
        transaction = mock()

        transaction.stub {
            on { replace(any(), any()) } doReturn transaction
        }
        val fragmentManager = mock<androidx.fragment.app.FragmentManager> {
            on { beginTransaction() } doReturn transaction
        }
        activity = mock {
            on { supportFragmentManager } doReturn fragmentManager
        }

        router = MainFragmentRouter(activity)
    }

    @Test
    fun testInitialRoute() {
        assertThat(router.currentRoute).isEqualTo(Route.HOME)
        val fragmentCaptor = argumentCaptor<androidx.fragment.app.Fragment>()
        verify(transaction).replace(any(), fragmentCaptor.capture())
        assertThat(fragmentCaptor.lastValue).isInstanceOf(HomeFragment::class.java)
    }

    @Test
    fun testHistory() {
        // Given
        val route = Route.HISTORY

        // When
        router.routeTo(route)

        // Then
        val fragmentCaptor = argumentCaptor<androidx.fragment.app.Fragment>()
        verify(transaction, times(2)).replace(any(), fragmentCaptor.capture())
        assertThat(fragmentCaptor.lastValue).isInstanceOf(HistoryFragment::class.java)
    }

    @Test
    fun testHome() {
        // Given
        val route = Route.HOME

        // When
        router.routeTo(route)

        // Then
        val fragmentCaptor = argumentCaptor<androidx.fragment.app.Fragment>()
        verify(transaction, times(2)).replace(any(), fragmentCaptor.capture())
        assertThat(fragmentCaptor.lastValue).isInstanceOf(HomeFragment::class.java)
    }

    @Test
    fun testFeed() {
        // Given
        val route = Route.FEED

        // When
        router.routeTo(route)

        // Then
        val fragmentCaptor = argumentCaptor<androidx.fragment.app.Fragment>()
        verify(transaction, times(2)).replace(any(), fragmentCaptor.capture())
        assertThat(fragmentCaptor.lastValue).isInstanceOf(FeedFragment::class.java)
    }

    @Test
    fun testDiary() {
        // Given
        val route = Route.GUIDANCE

        // When
        router.routeTo(route)

        // Then
        val fragmentCaptor = argumentCaptor<androidx.fragment.app.Fragment>()
        verify(transaction, times(2)).replace(any(), fragmentCaptor.capture())
        assertThat(fragmentCaptor.lastValue).isInstanceOf(CompeteFriendFragment::class.java)
    }

    @Test
    fun testState() {
        // Given
        val parcel = Parcel.obtain()

        // When
        router.state.writeToParcel(parcel, router.state.describeContents())
        parcel.setDataPosition(0)
        val state = Router.RouterState.createFromParcel(parcel)

        // Then
        assertThat(state).isEqualTo(router.state)
    }

    @Test
    fun testSetState() {
        // Given
        val state = Router.RouterState(Route.HISTORY)

        // When
        router.state = state

        // Then
        val fragmentCaptor = argumentCaptor<androidx.fragment.app.Fragment>()
        verify(transaction, times(2)).replace(any(), fragmentCaptor.capture())
        assertThat(fragmentCaptor.lastValue).isInstanceOf(HistoryFragment::class.java)
    }
}

