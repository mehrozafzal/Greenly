package greenely.greenely.retailonboarding.mappers

import androidx.databinding.ObservableBoolean
import greenely.greenely.retailonboarding.models.CustomerInfoJson
import greenely.greenely.retailonboarding.models.CustomerInfoModel
import greenely.greenely.utils.NonNullObservableField
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class CustomerInfoMapperTest {

    @Test
    fun fromCustomerInfoModelToJsonTest() {

        val mapper = CustomerInfoMapper()
        val json = CustomerInfoModel(
                personalNumber = NonNullObservableField("19930111-1238"),
                address = NonNullObservableField("Test address"),
                postalCode = NonNullObservableField("12345"),
                postalRegion = NonNullObservableField("Johannesov"),
                email = NonNullObservableField("test@greenely.se"),
                phoneNumber = NonNullObservableField("12345678910"),
                userTermsAccepted = ObservableBoolean(true),
                powerOfAttorneyTermsAccepted = ObservableBoolean(true)
        )

        // When
        val result = mapper.from(json)

        // Then
        assertThat(result).isEqualTo(
                CustomerInfoJson(
                        personal_number = "19930111-1238",
                        address = "Test address",
                        zip_code = "12345",
                        city = "Johannesov",
                        invoice_email = "test@greenely.se",
                        cell_phone = "12345678910",
                        promocode = null,
                        poaProcessRequired = false
                )
        )
    }
}
