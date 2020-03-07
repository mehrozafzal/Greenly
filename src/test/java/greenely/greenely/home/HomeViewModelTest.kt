package greenely.greenely.home

import android.app.Activity
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.nhaarman.mockito_kotlin.*
import greenely.greenely.home.data.HomeModelFactory
import greenely.greenely.home.data.HomeRepo
import greenely.greenely.home.models.HomeModel
import greenely.greenely.home.models.HomeResponse
import greenely.greenely.home.ui.HomeViewModel
import greenely.greenely.home.ui.events.Event
import greenely.greenely.main.ui.MainActivity
import greenely.greenely.models.Resource
import greenely.greenely.retail.util.RetailStateHandler
import io.reactivex.Observable
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.Test

@Suppress("KDocMissingDocumentation")
class HomeViewModelTest {

    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    private val repo = mock<HomeRepo>()
    private val homeModel = mock<HomeModel>()
    private val homeModelFactory = mock<HomeModelFactory> {
        on { createHomeModel(any()) } doReturn homeModel
    }

    private val retailStateHander=mock<RetailStateHandler>()


    private val viewModel = HomeViewModel(repo, homeModelFactory,retailStateHander)

    @Test
    fun testGetHomeModel() {
        // Given
        val eventObserver: Observer<Event> = mock()
        viewModel.events.observeForever(eventObserver)

        val response = mock<HomeResponse>()
        repo.stub {
            on { getHome(any()) } doReturn Observable.just(response)
        }
        val mockObserver = mock<Observer<Resource<HomeModel>>>()

        // When
        viewModel.getHomeModel().observeForever(mockObserver)

        // Then
        verify(mockObserver).onChanged(Resource.Success(homeModel))
        verify(homeModelFactory).createHomeModel(response)

        val event = argumentCaptor<Event>()
        verify(eventObserver, times(1)).onChanged(event.capture())
        assertThat(event.firstValue).isEqualTo(Event.LoaderUIEvent(false))
        assertThat(event.lastValue).isEqualTo(Event.LoaderUIEvent(false))
    }

    @Test
    fun testGetHomeModel_error() {
        // Given

        val eventObserver: Observer<Event> = mock()
        viewModel.events.observeForever(eventObserver)

        val errorObserver: Observer<Throwable?> = mock()
        viewModel.errors.observeForever(errorObserver)

        val error = Error()
        val response = mock<HomeResponse>()
        repo.stub {
            on { getHome(any()) } doReturn listOf<Observable<HomeResponse>>(
                    Observable.error(error),
                    Observable.just(response)
            )
        }
        val mockObserver = mock<Observer<Resource<HomeModel>>>()

        // When
        viewModel.getHomeModel().observeForever(mockObserver)
        viewModel.getHomeModel()

        // Then
        verify(errorObserver).onChanged(error)

        val event = argumentCaptor<Event>()
        verify(eventObserver, times(2)).onChanged(event.capture())
        assertThat(event.firstValue).isEqualTo(Event.LoaderUIEvent(false))
        assertThat(event.lastValue).isEqualTo(Event.LoaderUIEvent(false))
    }

    @Test
    fun testOnActivityResult() {

        val eventObserver: Observer<Event> = mock()
        viewModel.events.observeForever(eventObserver)

        repo.stub {
            on { getHome(any()) } doReturn Observable.empty()
        }

        viewModel.getHomeModel()
        viewModel.onActivityResult(MainActivity.SIGN_POA_REQUEST, Activity.RESULT_OK, null)

        verify(repo, times(2)).getHome(any())
    }

    @Test
    fun testOnClickFetchData() {
        val events = mutableListOf<Event>()
        viewModel.events.observeForever {
            it?.let { events += it }
        }

        viewModel.onClickFetchData()

        assertThat(events.last())
                .isEqualTo(
                        Event.StartSignatureNavigationEvent(MainActivity.SIGN_POA_REQUEST)
                )
    }
}