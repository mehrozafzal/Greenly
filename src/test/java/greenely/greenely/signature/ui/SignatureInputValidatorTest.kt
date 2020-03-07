package greenely.greenely.signature.ui

import android.app.Application
import android.graphics.Bitmap
import android.telephony.PhoneNumberUtils
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import greenely.greenely.R
import greenely.greenely.signature.ui.models.SignatureInputErrorModel
import greenely.greenely.signature.ui.models.SignatureInputModel
import greenely.greenely.signature.ui.validation.SignatureInputValidator
import greenely.greenely.utils.validation.PhoneNumberValidator
import org.assertj.core.api.Assertions
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareOnlyThisForTest
import org.powermock.modules.junit4.PowerMockRunner

@RunWith(PowerMockRunner::class)
@PrepareOnlyThisForTest(PhoneNumberUtils::class)
class SignatureInputValidatorTest {
    private lateinit var application: Application
    private lateinit var validator: SignatureInputValidator
    private lateinit var phoneNumberValidator: PhoneNumberValidator
    private lateinit var inputModel: SignatureInputModel
    private lateinit var errorModel: SignatureInputErrorModel
    private var signatureErrors = SignatureInputErrorModel()
    private var noSignatureErrors = SignatureInputErrorModel()
    private var emptySignatureErrors = SignatureInputErrorModel()

    @Mock
    private lateinit var bitmapMock: Bitmap

    @Before
    fun setUp() {
        application = mock {
            on { getString(R.string.can_not_be_empty) } doReturn "Can not be empty"
            on { getString(R.string.can_only_contain_digits) } doReturn "Can only contain digits"
            on { getString(R.string.does_not_contain_full_digits) } doReturn "Does not contain full digits"
            on { getString(R.string.personal_number_not_long_enough) } doReturn "personal_number_not_long_enough"
            on { getString(R.string.personal_number_not_valid) } doReturn "personal_number_not_valid"
            on { getString(R.string.invalid_facility_id) } doReturn "invalid_facility_id"
            on { getString(R.string.phone_number_must_be_10_digits) } doReturn "phone_number_must_be_10_digits"
            on { getString(R.string.phone_number_required) } doReturn "phone_number_required"
            on { getString(R.string.invalid_phone_number_start_with_07) } doReturn "invalid_phone_number_start_with_07"
        }

        phoneNumberValidator = mock { on { validate(kotlin.String()) } doReturn "" }

        validator = SignatureInputValidator(application, phoneNumberValidator)
        errorModel = SignatureInputErrorModel()
        inputModel = SignatureInputModel()

        noSignatureErrors = SignatureInputErrorModel("", "", "", "", "")
        signatureErrors = SignatureInputErrorModel(
                "Can not be empty",
                "Can not be empty",
                "personal_number_not_valid",
                "invalid_facility_id",
                "Can not be empty"
               )

        emptySignatureErrors = SignatureInputErrorModel(
                "Can not be empty",
                "Can not be empty",
                "personal_number_not_long_enough",
                "invalid_facility_id",
                "Can not be empty"
        )

        PowerMockito.mockStatic(PhoneNumberUtils::class.java)
    }

    @Test
    fun testInvalid() {
        //Given
        inputModel.firstName.set("")
        inputModel.lastName.set("")
        inputModel.personalNumber.set("123456781111")
        inputModel.address.set("")
        inputModel.postalCode.set("1")
        inputModel.postalRegion.set("")
        inputModel.phoneNumber.set("1234")

        // When
        val error = validator.validateAllSteps(inputModel)

        // Then
        Assertions.assertThat(error).isEqualToComparingFieldByField(signatureErrors)
    }

    @Test
    fun testEmpty() {
        //Given
        inputModel.firstName.set("")
        inputModel.lastName.set("")
        inputModel.personalNumber.set("")
        inputModel.address.set("")
        inputModel.postalCode.set("")
        inputModel.postalRegion.set("")
        inputModel.phoneNumber.set("")

        // When
        val error = validator.validateAllSteps(inputModel)

        // Then
        Assertions.assertThat(error).isEqualToComparingFieldByField(emptySignatureErrors)
    }

    @Test
    fun testValid() {
        //Given
        inputModel.firstName.set("John")
        inputModel.lastName.set("Smith")
        inputModel.personalNumber.set("199112068482")
        inputModel.address.set("address")
        inputModel.postalCode.set("10000")
        inputModel.postalRegion.set("region")
        inputModel.phoneNumber.set("0712345678")

        // When
        val error = validator.validateAllSteps(inputModel)

        // Then
        Assertions.assertThat(error).isEqualToComparingFieldByField(noSignatureErrors)
    }
}