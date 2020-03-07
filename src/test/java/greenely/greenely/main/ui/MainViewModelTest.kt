@file:Suppress("KDocMissingDocumentation")

package greenely.greenely.main.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.nhaarman.mockito_kotlin.*
import greenely.greenely.feed.data.FeedRepo
import greenely.greenely.main.events.Event
import greenely.greenely.main.router.Route
import greenely.greenely.retail.util.RetailStateHandler
import greenely.greenely.store.UserStore
import io.reactivex.Observable
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MainViewModelTest {
    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    private lateinit var userStore: UserStore
    private lateinit var feedRepo: FeedRepo
    private lateinit var viewModel: MainViewModel


    @Before
    fun setUp() {
        feedRepo = mock()
        userStore = mock()

        viewModel = MainViewModel(userStore, feedRepo)
    }

    @Test
    fun testHasUnreadFeedItems() {
        // Given
        feedRepo.stub {
            on { hasUnreadItems() } doReturn Observable.just(true)
        }
        val mockObserver: Observer<Boolean> = mock()

        // When
        viewModel.hasUnreadFeedItems.observeForever(mockObserver)

        // Then
        verify(mockObserver).onChanged(true)
    }

    @Test
    fun testHasNoUnreadFeedItems() {
        // Given
        feedRepo.stub {
            on { hasUnreadItems() } doReturn Observable.just(false)
        }
        val mockObserver: Observer<Boolean> = mock()

        // When
        viewModel.hasUnreadFeedItems.observeForever(mockObserver)

        // Then
        verify(mockObserver).onChanged(false)
    }

    @Test
    fun testReadFeedItems() {
        // Given
        feedRepo.stub {
            on { hasUnreadItems() } doReturn Observable.just(true)
        }
        val mockObserver: Observer<Boolean> = mock()
        viewModel.hasUnreadFeedItems.observeForever(mockObserver)

        // When
        viewModel.onRouteSelected(Route.FEED)

        // Then
        verify(mockObserver).onChanged(true)
        verify(mockObserver).onChanged(false)
    }

    @Test
    fun testLogout() {
        val events = mutableListOf<Event>()
        viewModel.events.observeForever {
            it?.let { events += it }
        }

        viewModel.logout()

        verify(userStore, times(1)).token = null

        assertThat(events.last()).isEqualTo(Event.Logout)
    }
}

