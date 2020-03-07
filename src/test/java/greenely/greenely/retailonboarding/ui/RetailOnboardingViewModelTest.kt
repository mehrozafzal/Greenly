package greenely.greenely.retailonboarding.ui

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.stub
import com.nhaarman.mockito_kotlin.verify
import greenely.greenely.retailonboarding.data.RetailOnboardingRepo
import greenely.greenely.retailonboarding.ui.validations.CustomerInfoValidator
import greenely.greenely.signature.data.models.PrefillDataResponseModel
import greenely.greenely.signature.ui.models.PreFillInfo
import io.reactivex.Single
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.schedulers.Schedulers
import org.assertj.core.api.Assertions
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class RetailOnboardingViewModelTest {
    @JvmField
    @Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var retailOnboardingViewModel: RetailOnboardingViewModel
    private lateinit var repo: RetailOnboardingRepo
    private lateinit var customerInfoValidator: CustomerInfoValidator
    private lateinit var context: Application

    @Before
    fun setUp() {
        RxAndroidPlugins.setMainThreadSchedulerHandler { Schedulers.from { it.run() } }

        repo = mock()
        context= mock()
        customerInfoValidator = mock()
        retailOnboardingViewModel = RetailOnboardingViewModel(context,customerInfoValidator, repo)
    }

    @After
    fun tearDown() {
        RxAndroidPlugins.reset()
    }

    @Test
    fun testPreFillFromPersonalNumber() {

        //Given
        val mockResponseObserver = mock<Observer<PreFillInfo>>()

        repo.stub {
            on { preFillFromPersonalNumber("1234") } doReturn Single.just(
                    PrefillDataResponseModel(
                            "",
                            "",
                            "En gata 1234",
                            "A postal region",
                            "12345"
                    )
            )
        }

        //When
        retailOnboardingViewModel.preFillFromPersonalNumber("1234").observeForever(mockResponseObserver)

        //Then
        verify(repo).preFillFromPersonalNumber("1234")

        Assertions.assertThat(retailOnboardingViewModel.customerInfoInput.address.get()).isEqualTo("En gata 1234")
        Assertions.assertThat(retailOnboardingViewModel.customerInfoInput.postalRegion.get()).isEqualTo("A postal region")
        Assertions.assertThat(retailOnboardingViewModel.customerInfoInput.postalCode.get()).isEqualTo("12345")
    }

}