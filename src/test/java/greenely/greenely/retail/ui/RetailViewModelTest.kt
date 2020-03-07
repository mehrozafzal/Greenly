package greenely.greenely.retail.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.nhaarman.mockito_kotlin.*
import greenely.greenely.retail.util.RetailStateHandler
import greenely.greenely.retail.data.RetailOverviewFactory
import greenely.greenely.retail.data.RetailRepo
import greenely.greenely.retail.models.RetailOverViewJson
import greenely.greenely.retail.models.RetailOverview
import greenely.greenely.retail.ui.events.Event
import greenely.greenely.store.UserStore
import io.reactivex.Observable
import org.assertj.core.api.Assertions
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

class RetailViewModelTest {
    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    private lateinit var userStore: UserStore

    private val repo = mock<RetailRepo>()
    private val model = mock<RetailOverview>()
    private val retailModelFactory = mock<RetailOverviewFactory> {
        on { createRetailOverviewModel(any()) } doReturn model
    }

    private val retailStateHander=mock<RetailStateHandler>()

    private lateinit var viewModel: RetailViewModel

    @Before
    fun setUp(){
        userStore = Mockito.mock(UserStore::class.java)
        viewModel = RetailViewModel(userStore, repo, retailModelFactory,retailStateHander)
    }

    @Test
    fun testGetRetailOverviewModel() {
        // Given
        val eventObserver: Observer<Event> = mock()
        viewModel.events.observeForever(eventObserver)

        val response = mock<RetailOverViewJson>()
        repo.stub {
            on { getRetailOverviewResponse() } doReturn Observable.just(response)
        }
        val mockObserver = mock<Observer<RetailOverview>>()

        // When
        viewModel.retailOverview.observeForever(mockObserver)

        // Then
        verify(mockObserver).onChanged(model)
        verify(retailModelFactory).createRetailOverviewModel(response)

        val event = argumentCaptor<Event>()
        verify(eventObserver, times(3)).onChanged(event.capture())
        Assertions.assertThat(event.firstValue).isEqualTo(Event.VisibilityOfViewUIEvent(false))
        Assertions.assertThat(event.lastValue).isEqualTo(Event.LoaderUIEvent(false))
    }

    @Test
    fun testGetRetailOverviewModel_error() {
        // Given

        val eventObserver: Observer<Event> = mock()
        viewModel.events.observeForever(eventObserver)

        val errorObserver: Observer<Throwable?> = mock()
        viewModel.errors.observeForever(errorObserver)

        val error = Error()
        val response = mock<RetailOverViewJson>()
        repo.stub {
            on { getRetailOverviewResponse() } doReturn listOf<Observable<RetailOverViewJson>>(
                    Observable.error(error),
                    Observable.just(response)
            )
        }
        val mockObserver = mock<Observer<RetailOverview>>()

        // When
        viewModel.retailOverview.observeForever(mockObserver)

        // Then
        verify(errorObserver).onChanged(error)

        val event = argumentCaptor<Event>()
        verify(eventObserver, times(3)).onChanged(event.capture())
        Assertions.assertThat(event.firstValue).isEqualTo(Event.VisibilityOfViewUIEvent(false))
        Assertions.assertThat(event.lastValue).isEqualTo(Event.LoaderUIEvent(false))
    }
}