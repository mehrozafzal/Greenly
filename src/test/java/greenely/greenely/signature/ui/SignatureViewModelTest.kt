package greenely.greenely.signature.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.nhaarman.mockito_kotlin.*
import greenely.greenely.retail.util.RetailStateHandler
import greenely.greenely.signature.data.SignatureRepo
import greenely.greenely.signature.data.models.InputValidationModel
import greenely.greenely.signature.data.models.PrefillDataResponseModel
import greenely.greenely.signature.data.models.ValidationResponse
import greenely.greenely.signature.data.models.ValidationsList
import greenely.greenely.signature.mappers.PreFillDataRequestMapper
import greenely.greenely.signature.mappers.SignatureInputMapper
import greenely.greenely.signature.mappers.SignatureRequestModelMapper
import greenely.greenely.signature.ui.models.PreFillInfo
import greenely.greenely.signature.ui.models.SignatureInputModel
import greenely.greenely.signature.ui.validation.SignatureInputValidator
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.schedulers.Schedulers
import org.assertj.core.api.Assertions.assertThat
import org.junit.*

class SignatureViewModelTest {
    @JvmField
    @Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var preFillRequestMapper: PreFillDataRequestMapper
    private lateinit var inputMapper: SignatureInputMapper
    private lateinit var requestMapper: SignatureRequestModelMapper
    private lateinit var repo: SignatureRepo
    private lateinit var inputValidator: SignatureInputValidator
    private lateinit var viewModel: SignatureViewModel
    private lateinit var inputValidationModel: InputValidationModel
    private lateinit var retailStateHandler: RetailStateHandler


    @Before
    fun setUp() {
        RxAndroidPlugins.setMainThreadSchedulerHandler { Schedulers.from { it.run() } }
        preFillRequestMapper = mock {
            on { fromSignatureInputModel(any()) } doReturn "1234"
        }

        inputMapper = mock {
            on { updateFromPreFillResponse(any(), any()) } doAnswer {
                val model = it.arguments[0] as SignatureInputModel
                model.firstName.set("Anton")
                model.lastName.set("Holmberg")
                model.address.set("En gata 1234")
                model.postalCode.set("12345")
                model.postalRegion.set("A postal region")
            }
        }

        inputValidationModel = mock {
            ValidationsList("1234")
        }

        requestMapper = mock()

        repo = mock()

        inputValidator = mock()

        retailStateHandler=mock()

        viewModel = SignatureViewModel(
                repo,
                preFillRequestMapper,
                inputMapper,
                requestMapper,
                inputValidator,
                retailStateHandler
        )
    }

    @After
    fun tearDown() {
        RxAndroidPlugins.reset()
    }

    @Test
    fun testPreFillFromPersonalNumber() {
        // Given
        val mockResponseObserver = mock<Observer<PreFillInfo>>()
        repo.stub {
            on { preFillFromPersonalNumber("1234") } doReturn Single.just(
                    PrefillDataResponseModel(
                            "Anton",
                            "Holmberg",
                            "En gata 1234",
                            "A postal region",
                            "12345"
                    )
            )
        }
        viewModel.signatureInput.personalNumber.set("1234")

        // When
        viewModel.preFillFromPersonalNumber().observeForever(mockResponseObserver)

        // Then
        val inputCaptor = argumentCaptor<SignatureInputModel>()
        verify(preFillRequestMapper).fromSignatureInputModel(inputCaptor.capture())

        assertThat(inputCaptor.lastValue.personalNumber.get()).isEqualTo("1234")

        verify(repo).preFillFromPersonalNumber("1234")

        verify(mockResponseObserver).onChanged(PreFillInfo.Success(true))

        assertThat(viewModel.signatureInput.firstName.get()).isEqualTo("Anton")
        assertThat(viewModel.signatureInput.lastName.get()).isEqualTo("Holmberg")
        assertThat(viewModel.signatureInput.address.get()).isEqualTo("En gata 1234")
        assertThat(viewModel.signatureInput.postalRegion.get()).isEqualTo("A postal region")
        assertThat(viewModel.signatureInput.postalCode.get()).isEqualTo("12345")
    }

    @Test
    fun testValidateInput() {
        //Given
        val mockResponseObserver = mock<Observer<Boolean>>()
        val mockRequest = InputValidationModel(ValidationsList("1234"))

        repo.stub {
            on { validateMeterId(mockRequest) } doReturn Observable.just(
                    ValidationResponse(
                            null,
                            null
                    )
            )
        }
        viewModel.signatureInput.personalNumber.set("1234")

        // When
        viewModel.validateInput().observeForever(mockResponseObserver)

        // Then
        verify(repo).validateMeterId(mockRequest)

        verify(mockResponseObserver).onChanged(true)

        assertThat(viewModel.signatureErrors.get()?.personalNumberError).isEqualTo("")

    }

    @Test
    fun testValidateInputError() {
        //Given
        val mockResponseObserver = mock<Observer<Boolean>>()
        val mockRequest = InputValidationModel(ValidationsList("1234"))

        repo.stub {
            on { validateMeterId(mockRequest) } doReturn Observable.just(
                    ValidationResponse(
                            "Error",
                            "Error"
                    )
            )
        }
        viewModel.signatureInput.personalNumber.set("1234")

        // When
        viewModel.validateInput().observeForever(mockResponseObserver)

        // Then
        verify(repo).validateMeterId(mockRequest)

        verify(mockResponseObserver).onChanged(false)

        assertThat(viewModel.signatureErrors.get()?.personalNumberError).isEqualTo("Error")

    }

    @Test
    fun testValidateInputRequestError() {
        //Given
        val mockResponseObserver = mock<Observer<Boolean>>()
        val mockErrorObserver = mock<Observer<Throwable>>()
        val error = Error()
        repo.stub {
            on {
                validateMeterId(
                        InputValidationModel(ValidationsList("1234"))
                )
            } doReturn Observable.error(
                    error
            )
        }
        viewModel.signatureInput.personalNumber.set("1234")
        viewModel.errors.observeForever(mockErrorObserver)
        // When
        viewModel.validateInput().observeForever(mockResponseObserver)

        // Then
        val infoCaptor = argumentCaptor<Boolean>()
        verify(mockResponseObserver).onChanged(infoCaptor.capture())

        assertThat(infoCaptor.lastValue).isEqualTo(false)

        verify(mockErrorObserver).onChanged(error)

    }

}
