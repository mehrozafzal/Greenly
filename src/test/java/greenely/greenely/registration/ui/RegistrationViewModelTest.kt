@file:Suppress("KDocMissingDocumentation")

package greenely.greenely.registration.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.nhaarman.mockito_kotlin.*
import greenely.greenely.accountsetup.models.AccountSetupNext
import greenely.greenely.models.AuthenticationInfo
import greenely.greenely.models.IntercomInfo
import greenely.greenely.models.Resource
import greenely.greenely.registration.data.RegistrationRepo
import greenely.greenely.registration.mappers.RegistrationRequestMapper
import greenely.greenely.registration.ui.events.Event
import greenely.greenely.registration.ui.models.RegistrationErrorModel
import greenely.greenely.registration.ui.validation.RegistrationInputValidator
import io.reactivex.Observable
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class RegistrationViewModelTest {

    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    private val repo: RegistrationRepo = mock()
    private val validator = mock<RegistrationInputValidator>()
    private val mapper = RegistrationRequestMapper()

    val viewModel: RegistrationViewModel = RegistrationViewModel(repo, validator, mapper)

    @Test
    fun testRegistrationApiError() {
        // Given
        val error = Error()
        repo.stub {
            on { register(any()) } doReturn Observable.error(error)
        }
        validator.stub {
            on { validate(any()) } doReturn RegistrationErrorModel.noErrors()

        }
        val mockObserver = mock<Observer<Resource<AuthenticationInfo>>>()
        viewModel.authenticationInfo.observeForever(mockObserver)

        // When
        viewModel.register()

        // Then
        verify(mockObserver).onChanged(Resource.Error(error))
    }

    @Test
    fun testNavigateBack() {
        // Given
        val mockObserver = mock<Observer<Event>>()
        viewModel.events.observeForever(mockObserver)

        // When
        viewModel.onBackPressed()

        // Then
        verify(mockObserver).onChanged(Event.Exit)
    }

    @Test
    fun testRegistrationSuccess() {
        // Given
        val intercom = IntercomInfo("1", "hash", "anton.holmberg@greenely.se")

        val authenticationInfo = AuthenticationInfo(1, AccountSetupNext.POA, mapOf(), intercom)
        repo.stub {
            on { register(any()) } doReturn Observable.just(authenticationInfo)
        }
        validator.stub {
            on { validate(any()) } doReturn RegistrationErrorModel.noErrors()
        }
        val mockObserver = mock<Observer<Resource<AuthenticationInfo>>>()
        viewModel.authenticationInfo.observeForever(mockObserver)

        // When
        viewModel.register()

        // Then
        verify(mockObserver).onChanged(Resource.Success(authenticationInfo))
    }
}
