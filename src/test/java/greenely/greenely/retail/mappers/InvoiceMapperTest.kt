package greenely.greenely.retail.mappers

import android.app.Application
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import greenely.greenely.R
import greenely.greenely.retail.models.Invoice
import greenely.greenely.retail.models.InvoiceJson
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

class InvoiceMapperTest {

    private lateinit var application: Application

    @Before
    fun setUp() {
        application = mock {
            on { getString(R.string.currency_format) } doReturn "%d kr"
        }
    }

    @Test
    fun fromInvoiceJsonTest() {

        val mapper = InvoiceMapper(application)
        val json = InvoiceJson(
                month = "December",
                state = "paid",
                isPaid = true,
                pdfUrl = "url",
                cost = 35100
        )

        // When
        val result = mapper.fromInvoiceJson(json)

        // Then
        assertThat(result).isEqualTo(
                Invoice(
                        "December",
                        "paid",
                        true,
                        "url",
                        "351 kr"
                )
        )
    }

}
