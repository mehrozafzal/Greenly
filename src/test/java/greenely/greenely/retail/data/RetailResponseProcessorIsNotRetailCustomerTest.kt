package greenely.greenely.retail.data

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import greenely.greenely.retail.mappers.MonthlyFeeMapper
import greenely.greenely.retail.models.RetailOverViewJson
import greenely.greenely.retail.models.RetailOverview
import org.assertj.core.api.Assertions
import org.junit.Before
import org.junit.Test

class RetailResponseProcessorIsNotRetailCustomerTest {

    private val response: RetailOverViewJson = mock()
    private val unhandledResponse = mock<RetailOverViewJson>()
    private val retailModelBuilder = mock<RetailOverview.Builder>()
    private lateinit var monthlyFeeMapper: MonthlyFeeMapper

    @Before
    fun setUp() {

        monthlyFeeMapper = mock {
            on { fromMonthlyFeeJson(any()) } doReturn ""
        }
    }

    @Test
    fun testProcessResponse() {
        // Given
        val processor = ProcessCanNotBecomeRetailCustomer(retailModelBuilder, monthlyFeeMapper)

        // When
        val nextResponse = processor.processResponse(response)

        // Then
        Assertions.assertThat(nextResponse).isEqualTo(response)

        verify(retailModelBuilder).currentDay = null
        verify(retailModelBuilder).currentMonth = null
        verify(retailModelBuilder).nextDay = null
        verify(retailModelBuilder).invoices = null
        verify(retailModelBuilder).onBoardingAllowed = false
    }

    @Test
    fun testHandlesOnlyHasData() {
        // Given
        val processor = ProcessCanNotBecomeRetailCustomer(retailModelBuilder, monthlyFeeMapper)

        // When
        val nextResponse = processor.processResponse(unhandledResponse)

        // Then
        Assertions.assertThat(nextResponse).isEqualTo(unhandledResponse)
    }

    @Test
    fun testNext() {
        // Given
        val otherResponse = mock<RetailOverViewJson>()
        val next = mock<RetailResponseProcessor> {
            on { processResponse(response) }.thenReturn(otherResponse)
        }
        val processor = ProcessCanNotBecomeRetailCustomer(retailModelBuilder, monthlyFeeMapper)
        processor.next = next

        // When
        val nextResponse = processor.processResponse(response)

        // Then
        Assertions.assertThat(nextResponse).isEqualTo(otherResponse)
    }
}