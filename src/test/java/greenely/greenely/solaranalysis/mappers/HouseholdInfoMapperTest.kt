package greenely.greenely.solaranalysis.mappers

import androidx.databinding.ObservableInt
import greenely.greenely.R
import greenely.greenely.solaranalysis.models.HouseholdInfo
import greenely.greenely.solaranalysis.models.HouseholdInfoJson
import greenely.greenely.utils.NonNullObservableField
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized


@RunWith(Parameterized::class)
class HouseholdInfoMapperTest(
        private val input: HouseholdInfo,
        private val expectedOutput: HouseholdInfoJson
) {

    companion object {
        @Parameterized.Parameters
        @JvmStatic
        fun data(): List<Array<Any>> {
            val data = mutableListOf<Array<Any>>()
            listOf(
                    R.id.west to -80,
                    R.id.southWest to -40,
                    R.id.south to 0,
                    R.id.southEast to 50,
                    R.id.east to 90
            ).forEach { direction ->
                listOf(
                        R.id.small to 40,
                        R.id.medium to 53,
                        R.id.big to 68
                ).forEach { roofSize ->
                    listOf(
                            R.id.tightAngle to 5,
                            R.id.mediumAngle to 20,
                            R.id.wideAngle to 45
                    ).forEach { roofAngle ->
                        val input = HouseholdInfo(
                                NonNullObservableField("Street"),
                                NonNullObservableField("Postal code"),
                                NonNullObservableField("Postal region"),
                                ObservableInt(roofSize.first),
                                ObservableInt(roofAngle.first),
                                ObservableInt(direction.first)
                        )
                        val expectedOutput = HouseholdInfoJson(
                                "Street Postalcode Postal region",
                                roofAngle.second,
                                direction.second,
                                roofSize.second
                        )

                        data.add(arrayOf(input, expectedOutput))
                    }
                }
            }
            return data.toList()
        }
    }

    @Test
    fun testToJsonData() {
        // When
        val jsonData = HouseholdInfoMapper().toJsonData(input)

        // Then
        assertThat(jsonData).isEqualTo(expectedOutput)
    }

    @Test
    fun testToAddressValidationRequest() {
        // When
        val addressValidationRequest = HouseholdInfoMapper().toAddressValidationRequest(input)

        // Then
        assertThat(addressValidationRequest.address).isEqualTo(expectedOutput.address)
    }
}