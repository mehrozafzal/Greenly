package greenely.greenely.utils

import android.app.Application
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import greenely.greenely.R
import org.assertj.core.api.Assertions
import org.junit.Before
import org.junit.Test

class CurrencyFormatUtilsTest{
    private lateinit var application: Application
    private lateinit var currencyFormatter: CurrencyFormatUtils

    @Before
    fun setUp() {
        application = mock {
            on { getString(R.string.currency_symbol) } doReturn "kr"
        }
        currencyFormatter = CurrencyFormatUtils(application)
    }

    @Test
    fun testCurrencyFormatter() {
        // Given
        val currencyFormat = "1,000 kr"

        // When
        val value = currencyFormatter.currencyFormatter(1000)

        // Then
        Assertions.assertThat(value).isEqualTo(currencyFormat)
    }
}
