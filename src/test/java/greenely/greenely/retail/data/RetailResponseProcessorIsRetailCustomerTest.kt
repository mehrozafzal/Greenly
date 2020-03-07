package greenely.greenely.retail.data

import com.nhaarman.mockito_kotlin.*
import greenely.greenely.retail.mappers.CostMapper
import greenely.greenely.retail.mappers.InvoiceMapper
import greenely.greenely.retail.mappers.MonthlyFeeMapper
import greenely.greenely.retail.models.*
import org.assertj.core.api.Assertions
import org.junit.Before
import org.junit.Test

class RetailResponseProcessorIsRetailCustomerTest {

    private lateinit var response: RetailOverViewJson
    private lateinit var invoiceMapper: InvoiceMapper
    private lateinit var monthlyFeeMapper: MonthlyFeeMapper
    private lateinit var costMapper: CostMapper

    private val unhandledResponse = mock<RetailOverViewJson>()
    private val retailModelBuilder = mock<RetailOverview.Builder>()

    @Before
    fun setUp() {
        response = RetailOverViewJson(
                true,
                false,
                59,
                false,
                CurrentMonthDetailsJson(
                        "Title",
                        "Subtitle",
                        4000,
                        listOf(CurrentMonthPointsJson(100,"1551211293"), CurrentMonthPointsJson(200,"1551211294"))
                ),
                HeaderDetailsJson(
                        "Title",
                        "Subtitle",
                        4000,
                        listOf()
                ),
                HeaderDetailsJson(
                        "Title",
                        "Subtitle",
                        4000,
                        listOf(PricingPointsJson(1,"1551211293"), PricingPointsJson(200,"1551211294"))
                ),
                CustomerState.OPERATIONAL,
                "5 dec 2018",
                null,
                mutableListOf(),
                100f,
                listOf(
                        InvoiceJson(
                                "Month",
                                "paid",
                                true,
                                "https://somethi.ng/2",
                                35100
                        )
                )
        )

        costMapper = mock {
            on { formatCost(any(), eq(0)) } doReturn "40"
            on { formatCost(any(), eq(2)) } doReturn "40.00"
        }

        monthlyFeeMapper = mock {
            on { fromMonthlyFeeJson(any()) } doReturn "%s kr/mån"
        }

        invoiceMapper = mock {
            on { fromInvoiceJson(any()) } doReturn Invoice(
                    "Month",
                    "paid",
                    true,
                    "351 kr",
                    "https://somethi.ng/2"
            )
        }
    }

    @Test
    fun testProcessResponse() {
        // Given
        val processor = ProcessIsRetailCustomer(retailModelBuilder, invoiceMapper, costMapper)

        // When
        val nextResponse = processor.processResponse(response)

        // Then
        Assertions.assertThat(nextResponse).isEqualTo(response)

        verify(retailModelBuilder).currentDay = HeaderDetailsPricing(
                "Title",
                "Subtitle",
                "40.00",
                listOf(PricingPointsJson(1,"1551211293"), PricingPointsJson(200,"1551211294"))
        )
        verify(retailModelBuilder).currentMonth = HeaderDetailsConsumption(
                "Title",
                "Subtitle",
                "40",
                listOf(CurrentMonthPointsJson(100,"1551211293"), CurrentMonthPointsJson(200,"1551211294"))
        )
        verify(retailModelBuilder).nextDay = HeaderDetailsPricing(
                "Title",
                "Subtitle",
                "40.00",
                listOf()
        )
        verify(retailModelBuilder).invoices = listOf(
                Invoice(
                        "Month",
                        "paid",
                        true,
                        "351 kr",
                        "https://somethi.ng/2"
                )
        )
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