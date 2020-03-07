package greenely.greenely.solaranalysis.ui.householdinfo.validations

import android.app.Application
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import greenely.greenely.R
import greenely.greenely.solaranalysis.models.AddressErrors
import greenely.greenely.solaranalysis.models.HouseholdInfo
import org.assertj.core.api.Assertions
import org.junit.Before
import org.junit.Test

class AddressValidatorTest {

    private lateinit var application: Application
    private lateinit var validator: AddressValidator
    private lateinit var inputModel: HouseholdInfo
    private lateinit var errorModel: AddressErrors
    private var addressErrors = AddressErrors()
    private var noAddressErrors = AddressErrors()
    private var emptyAddressErrors = AddressErrors()

    @Before
    fun setUp() {
        application = mock {
            on { getString(R.string.can_not_be_empty) } doReturn "Can not be empty"
            on { getString(R.string.can_only_contain_digits) } doReturn "Can only contain digits"
            on { getString(R.string.does_not_contain_full_digits) } doReturn "Does not contain full digits"
        }

        validator = AddressValidator(application)
        errorModel = AddressErrors()
        inputModel = HouseholdInfo()

        noAddressErrors = AddressErrors("", "", "")
        addressErrors = AddressErrors(
                "Can not be empty",
                "Does not contain full digits",
                "Can not be empty")

        emptyAddressErrors = AddressErrors(
                "Can not be empty",
                "Can not be empty",
                "Can not be empty")

    }

    @Test
    fun testInvalid() {
        //Given
        inputModel.address.set("")
        inputModel.postalCode.set("123")
        inputModel.postalRegion.set("")

        // When
        val error = validator.validate(inputModel)

        // Then
        Assertions.assertThat(error).isEqualToComparingFieldByField(addressErrors)
    }

    @Test
    fun testEmpty() {
        //Given
        inputModel.address.set("")
        inputModel.postalCode.set("")
        inputModel.postalRegion.set("")

        // When
        val error = validator.validate(inputModel)

        // Then
        Assertions.assertThat(error).isEqualToComparingFieldByField(emptyAddressErrors)
    }

    @Test
    fun testValid() {
        //Given
        inputModel.address.set("address")
        inputModel.postalCode.set("10000")
        inputModel.postalRegion.set("region")

        // When
        val error = validator.validate(inputModel)

        // Then
        Assertions.assertThat(error).isEqualToComparingFieldByField(noAddressErrors)
    }
}