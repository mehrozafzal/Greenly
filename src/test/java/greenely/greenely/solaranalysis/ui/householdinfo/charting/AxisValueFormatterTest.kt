
package greenely.greenely.solaranalysis.ui.householdinfo.charting

import org.assertj.core.api.Assertions.*
import org.junit.Before
import org.junit.Test

class AxisValueFormatterTest {
    private lateinit var formatter: AxisValueFormatter

    @Before
    fun setUp() {
        formatter = AxisValueFormatter()
    }

    @Test
    fun testFormat() {
        // Given
        val months = listOf(
                "J",
                "F",
                "M",
                "A",
                "M",
                "J",
                "J",
                "A",
                "S",
                "O",
                "N",
                "D"
        )

        // When
        val formatted = months.mapIndexed { index, _ ->
            formatter.getFormattedValue(index.toFloat(), null)
        }

        // Then
        assertThat(formatted).isEqualTo(months)
    }
}
