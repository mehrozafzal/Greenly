package greenely.greenely.solaranalysis.ui.householdinfo.validations

import android.app.Application
import androidx.databinding.ObservableField
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import greenely.greenely.R
import greenely.greenely.solaranalysis.models.ContactInfo
import greenely.greenely.solaranalysis.models.ContactInfoErrors
import greenely.greenely.utils.validation.EmailValidator
import greenely.greenely.utils.validation.PhoneNumberValidator
import org.assertj.core.api.Assertions
import org.junit.Before
import org.junit.Test

class ContactInfoValidatorTest{

    private lateinit var application: Application
    private lateinit var validator: ContactInfoValidator
    private lateinit var emailValidator: EmailValidator
    private lateinit var phoneNoValidator: PhoneNumberValidator
    private lateinit var inputModel: ContactInfo
    private lateinit var errorModel: ContactInfoErrors
    private var contactInfoErrors = ContactInfoErrors()
    private var noContactInfoErrors = ContactInfoErrors()

    @Before
    fun setUp() {
        application = mock {
            on { getString(R.string.name_required) } doReturn "Required"
            on { getString(R.string.can_only_contain_digits) } doReturn "Can only contain digits"
            on { getString(R.string.does_not_contain_full_digits) } doReturn "Does not contain full digits"
        }

        emailValidator = mock { on { validate(String())} doReturn "" }
        phoneNoValidator = mock { on { validate(String())} doReturn "" }

        validator = ContactInfoValidator(application, emailValidator, phoneNoValidator )
        errorModel = ContactInfoErrors()
        inputModel = ContactInfo()

        noContactInfoErrors = ContactInfoErrors(
                nameError = ObservableField(""),
                emailError = ObservableField(""),
                phoneNumberError = ObservableField("")
        )
        contactInfoErrors = ContactInfoErrors(
                nameError = ObservableField("Required"),
                emailError = ObservableField(""),
                phoneNumberError = ObservableField("")
        )


    }

    @Test
    fun testInvalid() {
        //Given
        inputModel.name.set("  ")
        inputModel.email.set("email")
        inputModel.phoneNumber.set("1234")

        // When
        val error = validator.validate(inputModel)

        // Then
        Assertions.assertThat(error).isEqualToComparingFieldByField(contactInfoErrors)
    }

    @Test
    fun testValid() {
        //Given
        inputModel.name.set("name")
        inputModel.email.set("email")
        inputModel.phoneNumber.set("1234")

        // When
        val error = validator.validate(inputModel)

        // Then
        Assertions.assertThat(error).isEqualToComparingFieldByField(noContactInfoErrors)
    }
}