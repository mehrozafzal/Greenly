package greenely.greenely.solaranalysis.ui.householdinfo

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.nhaarman.mockito_kotlin.*
import greenely.greenely.solaranalysis.data.SolarAnalysisRepo
import greenely.greenely.solaranalysis.models.Analysis
import greenely.greenely.solaranalysis.ui.householdinfo.events.Event
import greenely.greenely.solaranalysis.ui.householdinfo.validations.AddressValidator
import greenely.greenely.solaranalysis.ui.householdinfo.validations.ContactInfoValidator
import io.reactivex.Completable
import io.reactivex.Observable
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SolarAnalysisHouseholdInfoViewModelTest {

    @JvmField
    @Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var repo: SolarAnalysisRepo
    private lateinit var viewModel: SolarAnalysisViewModel
    private lateinit var addressValidator: AddressValidator
    private lateinit var contactInfoValidator: ContactInfoValidator
    private lateinit var analysis: Analysis

    @Before
    fun setUp() {
        repo = mock {
            on { sendHouseholdInfo(any()) } doReturn Observable.just(
                    Analysis(
                            "12",
                            "12 kr",
                            "12 kr",
                            "12 kr",
                            "12 kWh",
                            "12 years",
                            "12 years",
                            "12 years",
                            mutableListOf()
                    )
            )
        }
        analysis = Analysis(
                "12",
                "12 kr",
                "12 kr",
                "12 kr",
                "12 kWh",
                "12 years",
                "12 years",
                "12 years",
                mutableListOf()
        )
        addressValidator = mock()
        contactInfoValidator = mock()
        viewModel = SolarAnalysisViewModel(repo, addressValidator, contactInfoValidator)

    }

    @Test
    fun testGetAnalysisDetails() {
        // Given
        val mockObserver: Observer<Event> = mock()
        viewModel.events.observeForever(mockObserver)

        // When
        viewModel.getAnalysisDetails()

        // Then
        verify(repo).sendHouseholdInfo(viewModel.householdInfo)

        val event = argumentCaptor<Event>()
        verify(mockObserver, times(2)).onChanged(event.capture())

        assertThat(event.firstValue).isEqualTo(Event.ShowLoader(true))
        assertThat(event.lastValue).isEqualTo(Event.ShowLoader(false))
        assertThat(viewModel.analysis.get()).isEqualTo(analysis)
    }

    @Test
    fun testCompleteError() {
        // Given
        val mockObserver: Observer<Event> = mock()
        viewModel.events.observeForever(mockObserver)

        val error = Error()
        repo.stub {
            on { sendHouseholdInfo(any()) } doReturn Observable.error<Analysis>(error)
        }

        // When
        viewModel.getAnalysisDetails()

        // Then
        val event = argumentCaptor<Event>()
        verify(mockObserver, times(3)).onChanged(event.capture())

        assertThat(event.firstValue).isEqualTo(Event.ShowLoader(true))
        assertThat(event.secondValue).isEqualTo(Event.ShowLoader(false))
        assertThat((event.lastValue as Event.ShowError).error)
                .isEqualTo(error)
    }

    @Test
    fun testValidateAddress() {
        // Given
        val mockObserver: Observer<Event> = mock()
        viewModel.events.observeForever(mockObserver)

        val mockCallback = mock<(Throwable?) -> Unit>()
        repo.stub {
            on { validateAddress(any()) } doReturn Completable.complete()
        }

        // When
        viewModel.validateAddress(mockCallback)

        // Then
        verify(mockCallback).invoke(null)

        val event = argumentCaptor<Event>()
        verify(mockObserver, times(2)).onChanged(event.capture())
        assertThat(event.firstValue).isEqualTo(Event.ShowLoader(true))
        assertThat(event.lastValue).isEqualTo(Event.ShowLoader(false))
    }

    @Test
    fun testValidateAddressError() {
        // Given
        val mockObserver: Observer<Event> = mock()
        viewModel.events.observeForever(mockObserver)

        val mockCallback = mock<(Throwable?) -> Unit>()
        val error = Error()
        repo.stub {
            on { validateAddress(any()) } doReturn Completable.error(error)
        }

        // When
        viewModel.validateAddress(mockCallback)

        // Then
        verify(mockCallback).invoke(error)

        val event = argumentCaptor<Event>()
        verify(mockObserver, times(2)).onChanged(event.capture())
        assertThat(event.firstValue).isEqualTo(Event.ShowLoader(true))
        assertThat(event.lastValue).isEqualTo(Event.ShowLoader(false))
    }

    @Test
    fun testAbort() {
        // Given
        val mockObserver: Observer<Event> = mock()
        viewModel.events.observeForever(mockObserver)

        // When
        viewModel.abort()

        // Then
        verify(mockObserver).onChanged(Event.Abort)
    }

    @Test
    fun testRequestContact() {
        // Given
        val mockObserver: Observer<Event> = mock()
        viewModel.events.observeForever(mockObserver)
        viewModel.analysis.set(analysis)

        // When
        viewModel.requestContact()

        // Then
        verify(mockObserver).onChanged(Event.Done(viewModel.contactInfo, viewModel.householdInfo, analysis))
    }
}