package greenely.greenely.home.data

import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import greenely.greenely.R
import greenely.greenely.home.models.HistoricalComparisonModel
import greenely.greenely.home.models.HomeModel
import greenely.greenely.home.models.HomeResponse
import greenely.greenely.home.models.LatestComparisonModel
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

/**
 * A [ProcessHasData].
 */
class ProcessHasDataTest {

    private val response: HomeResponse = mock {
        on { state } doReturn DataState.HAS_DATA
        on { comparisonInfoText } doReturn "Comparison Info Text"
        on { comparisonInfoTitle } doReturn "Comparison Info Title"
    }

    private val unhandledResponse = mock<HomeResponse>()
    private val homeModelBuilder = mock<HomeModel.Builder>()

    /**
     * Should set the image according to the level.
     */
    @Test
    fun testProcessResponse() {
        // Given
        val processor = ProcessHasData(homeModelBuilder)

        // When
        val nextResponse = processor.processResponse(response)

        // Then
        assertThat(nextResponse).isEqualTo(response)

        verify(homeModelBuilder).extraText = null
        verify(homeModelBuilder).stateTitle = null
        verify(homeModelBuilder).comparisonInfoTitle = "Comparison Info Title"
        verify(homeModelBuilder).comparisonInfoText = "Comparison Info Text"
        verify(homeModelBuilder).isButtonVisible = false
    }

    /**
     * Should not handle other responses than [HasData].
     */
    @Test
    fun testHandlesOnlyHasData() {
        // Given
        val processor = ProcessHasData(homeModelBuilder)

        // When
        val nextResponse = processor.processResponse(unhandledResponse)

        // Then
        assertThat(nextResponse).isEqualTo(unhandledResponse)
    }

    /**
     * Should call the next part of the chain if there is one.
     */
    @Test
    fun testNext() {
        // Given
        val otherResponse = mock<HomeResponse>()
        val next = mock<HomeResponseProcessor> {
            on { processResponse(response) }.thenReturn(otherResponse)
        }
        val processor = ProcessHasData(homeModelBuilder)
        processor.next = next

        // When
        val nextResponse = processor.processResponse(response)

        // Then
        assertThat(nextResponse).isEqualTo(otherResponse)
    }
}

/**
 * A [ProcessMissingPoa].
 */
class ProcessMissingPoaTest {

    private val response: HomeResponse = mock {
        on { state } doReturn DataState.NEEDS_POA
        on { comparisonInfoText } doReturn "Comparison Info Text"
        on { comparisonInfoTitle } doReturn "Comparison Info Title"
    }

    private val unhandledResponse = mock<HomeResponse>()
    private val homeModelBuilder = mock<HomeModel.Builder>()

    /**
     * Should update the image.
     */
    @Test
    fun testProcessResponse() {
        // Given
        val processor = ProcessMissingPoa(homeModelBuilder)

        // When
        val nextResponse = processor.processResponse(response)

        // Then
        verify(homeModelBuilder).extraText = "Påbörja inhämtning av din data helt kostnadsfritt."
        verify(homeModelBuilder).stateTitle = "Hämta din förbrukningsdata!"
        verify(homeModelBuilder).comparisonInfoTitle = "Comparison Info Title"
        verify(homeModelBuilder).comparisonInfoText = "Comparison Info Text"
        verify(homeModelBuilder).isButtonVisible = true

        assertThat(nextResponse).isEqualTo(response)
    }

    /**
     * Should not handle other responses than [MissingPoa].
     */
    @Test
    fun testHandlesOnlyMissingPoa() {
        // Given
        val processor = ProcessMissingPoa(homeModelBuilder)

        // When
        val nextResponse = processor.processResponse(unhandledResponse)

        // Then
        assertThat(nextResponse).isEqualTo(unhandledResponse)
    }

    /**
     * Should call the next part of the chain if there is one.
     */
    @Test
    fun testNext() {
        // Given
        val otherResponse = mock<HomeResponse>()
        val next = mock<HomeResponseProcessor> {
            on { processResponse(response) }.thenReturn(otherResponse)
        }
        val processor = ProcessMissingPoa(homeModelBuilder)
        processor.next = next

        // When
        val nextResponse = processor.processResponse(response)

        // Then
        assertThat(nextResponse).isEqualTo(otherResponse)
    }
}

/**
 * A [ProcessWaiting].
 */
class ProcessWaitingTest {
    private val unhandledResponse = mock<HomeResponse>()

    private val response: HomeResponse = mock {
        on { state } doReturn DataState.WAITING
        on { comparisonInfoText } doReturn "Comparison Info Text"
        on { comparisonInfoTitle } doReturn "Comparison Info Title"
    }

    private val homeModelBuilder = mock<HomeModel.Builder>()

    /**
     * Should update the image.
     */
    @Test
    fun testProcessResponse() {
        // Given
        val processor = ProcessWaiting(homeModelBuilder)

        // When
        val nextResponse = processor.processResponse(response)

        // Then
        verify(homeModelBuilder).extraText = "Greenely pingar ditt elnätsbolag. Vi notifierar dig när du kan börja följa din elanvändning"
        verify(homeModelBuilder).stateTitle = "Väntar på data från ditt elnätsbolag…"
        verify(homeModelBuilder).comparisonInfoTitle = "Comparison Info Title"
        verify(homeModelBuilder).comparisonInfoText = "Comparison Info Text"
        verify(homeModelBuilder).isButtonVisible = false

        assertThat(nextResponse).isEqualTo(response)
    }

    /**
     * Should not handle other responses than [MissingPoa].
     */
    @Test
    fun testHandlesOnlyWaiting() {
        // Given
        val processor = ProcessWaiting(homeModelBuilder)

        // When
        val nextResponse = processor.processResponse(unhandledResponse)

        // Then
        assertThat(nextResponse).isEqualTo(unhandledResponse)
    }

    /**
     * Should call the next part of the chain if there is one.
     */
    @Test
    fun testNext() {
        // Given
        val otherResponse = mock<HomeResponse>()
        val next = mock<HomeResponseProcessor> {
            on { processResponse(response) }.thenReturn(otherResponse)
        }
        val processor = ProcessWaiting(homeModelBuilder)
        processor.next = next

        // When
        val nextResponse = processor.processResponse(response)

        // Then
        assertThat(nextResponse).isEqualTo(otherResponse)
    }
}

class ProcessWaitingForZavanneTest {
    private val unhandledResponse = mock<HomeResponse>()

    private val response: HomeResponse = mock {
        on { state } doReturn DataState.WAITING_FOR_ZAVANNE
        on { comparisonInfoText } doReturn "Comparison Info Text"
        on { comparisonInfoTitle } doReturn "Comparison Info Title"
    }

    private val homeModelBuilder = mock<HomeModel.Builder>()

    /**
     * Should update the image.
     */
    @Test
    fun testProcessResponse() {
        // Given
        val processor = ProcessWaitingForZavanne(homeModelBuilder)

        // When
        val nextResponse = processor.processResponse(response)

        // Then
        verify(homeModelBuilder).extraText = "Vi notifierar dig när du kan börja följa din elanvändning."
        verify(homeModelBuilder).stateTitle = "Kontaktar ditt elnätsbolag…"
        verify(homeModelBuilder).comparisonInfoTitle = "Comparison Info Title"
        verify(homeModelBuilder).comparisonInfoText = "Comparison Info Text"
        verify(homeModelBuilder).isButtonVisible = false

        assertThat(nextResponse).isEqualTo(response)
    }

    /**
     * Should not handle other responses than [MissingPoa].
     */
    @Test
    fun testHandlesOnlyWaitingForZavanne() {
        // Given
        val processor = ProcessWaitingForZavanne(homeModelBuilder)

        // When
        val nextResponse = processor.processResponse(unhandledResponse)

        // Then
        assertThat(nextResponse).isEqualTo(unhandledResponse)
    }

    /**
     * Should call the next part of the chain if there is one.
     */
    @Test
    fun testNext() {
        // Given
        val otherResponse = mock<HomeResponse>()
        val next = mock<HomeResponseProcessor> {
            on { processResponse(response) }.thenReturn(otherResponse)
        }
        val processor = ProcessWaitingForZavanne(homeModelBuilder)
        processor.next = next

        // When
        val nextResponse = processor.processResponse(response)

        // Then
        assertThat(nextResponse).isEqualTo(otherResponse)
    }
}

class ProcessZavanneErrorTest {
    private val unhandledResponse = mock<HomeResponse>()

    private val response: HomeResponse = mock {
        on { state } doReturn DataState.ZAVANNE_ERROR
        on { comparisonInfoText } doReturn "Comparison Info Text"
        on { comparisonInfoTitle } doReturn "Comparison Info Title"
    }

    private val homeModelBuilder = mock<HomeModel.Builder>()

    /**
     * Should update the image.
     */
    @Test
    fun testProcessResponse() {
        // Given
        val processor = ProcessZavanneError(homeModelBuilder)

        // When
        val nextResponse = processor.processResponse(response)

        // Then
        verify(homeModelBuilder).extraText = "Vänligen kontrollera att uppgifterna stämmer och försök igen. Skriv in till oss om det fortfarande inte skulle gå."
        verify(homeModelBuilder).stateTitle = "Ajdå, nu gick visst något fel"
        verify(homeModelBuilder).comparisonInfoTitle = "Comparison Info Title"
        verify(homeModelBuilder).comparisonInfoText = "Comparison Info Text"
        verify(homeModelBuilder).isButtonVisible = true

        assertThat(nextResponse).isEqualTo(response)
    }

    /**
     * Should not handle other responses than [MissingPoa].
     */
    @Test
    fun testHandlesOnlyZavanneError() {
        // Given
        val processor = ProcessZavanneError(homeModelBuilder)

        // When
        val nextResponse = processor.processResponse(unhandledResponse)

        // Then
        assertThat(nextResponse).isEqualTo(unhandledResponse)
    }

    /**
     * Should call the next part of the chain if there is one.
     */
    @Test
    fun testNext() {
        // Given
        val otherResponse = mock<HomeResponse>()
        val next = mock<HomeResponseProcessor> {
            on { processResponse(response) }.thenReturn(otherResponse)
        }
        val processor = ProcessZavanneError(homeModelBuilder)
        processor.next = next

        // When
        val nextResponse = processor.processResponse(response)

        // Then
        assertThat(nextResponse).isEqualTo(otherResponse)
    }
}

class ProcessComparisonDataTest {

    private val comparison =
            Comparison(
                    1496275200,
                    null,
                    135000,
                    107000
            )

    private val response: HomeResponse = mock {
        on { comparison } doReturn comparison
        on { comparisonMaxValue } doReturn 1f
        on { pointsMaxValue } doReturn 1f
        on { points } doReturn listOf(comparison)
        on { feedback } doReturn "text"
        on { resolution } doReturn "D"
    }

    private val unhandledResponse = mock<HomeResponse> {
        on { comparison } doReturn comparison
        on { resolution } doReturn "D"
    }


    private val homeModelBuilder = mock<HomeModel.Builder>()

    /**
     * Should update the image.
     */
    @Test
    fun testProcessResponse() {
        // Given
        val processor = ProcessComparisonData(homeModelBuilder)

        // When
        val nextResponse = processor.processResponse(response)

        // Then
        verify(homeModelBuilder).resolution = R.id.days
        verify(homeModelBuilder).comparisonMaxValue = 1f
        verify(homeModelBuilder).pointsMaxValue = 1f

        val latestComparisonModelCaptor = argumentCaptor<LatestComparisonModel>()

        verify(homeModelBuilder).latestComparisonModel = latestComparisonModelCaptor.capture()
        assertThat(nextResponse).isEqualTo(response)

        val latestComparisonModel = latestComparisonModelCaptor.firstValue

        assertThat(latestComparisonModel.title).isEqualTo(comparison.day)
        assertThat(latestComparisonModel.title).isEqualTo("text")
        assertThat(latestComparisonModel.comparison).isEqualTo(comparison)

        val historicalComparisonModelCaptor = argumentCaptor<HistoricalComparisonModel>()

        verify(homeModelBuilder).historicalComparisonModel = historicalComparisonModelCaptor.capture()
        assertThat(nextResponse).isEqualTo(response)

        val historicalComparisonModel = historicalComparisonModelCaptor.firstValue

        assertThat(historicalComparisonModel.title).isEqualTo(R.string.trend_resolution_days_title)
        assertThat(historicalComparisonModel.points).isEqualTo(listOf(comparison))

    }

    /**
     * Should not handle other responses than [MissingPoa].
     */
    @Test
    fun testHandlesOnlyComparisonData() {
        // Given
        val processor = ProcessComparisonData(homeModelBuilder)

        // When
        val nextResponse = processor.processResponse(unhandledResponse)

        // Then
        assertThat(nextResponse).isEqualTo(unhandledResponse)
    }

    /**
     * Should call the next part of the chain if there is one.
     */
    @Test
    fun testNext() {
        // Given
        val otherResponse = mock<HomeResponse>()
        val next = mock<HomeResponseProcessor> {
            on { processResponse(response) }.thenReturn(otherResponse)
        }
        val processor = ProcessComparisonData(homeModelBuilder)
        processor.next = next

        // When
        val nextResponse = processor.processResponse(response)

        // Then
        assertThat(nextResponse).isEqualTo(otherResponse)
    }
}
