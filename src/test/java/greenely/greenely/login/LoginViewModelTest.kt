@file:Suppress("KDocMissingDocumentation")

package greenely.greenely.login

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.nhaarman.mockito_kotlin.*
import greenely.greenely.accountsetup.models.AccountSetupNext
import greenely.greenely.login.data.LoginRepo
import greenely.greenely.login.model.LoginErrorModel
import greenely.greenely.login.ui.LoginViewModel
import greenely.greenely.login.ui.events.Event
import greenely.greenely.login.ui.validation.LoginInputValidation
import greenely.greenely.models.AuthenticationInfo
import greenely.greenely.models.Resource
import io.reactivex.Observable
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class LoginViewModelTest {

    @Rule
    @JvmField
    val executorRule = InstantTaskExecutorRule()

    lateinit var repo: LoginRepo

    lateinit var viewModel: LoginViewModel

    lateinit var validation: LoginInputValidation

    private lateinit var application: Application

    private lateinit var errorModel: LoginErrorModel

    @Before
    fun setUp() {


        repo = mock()
        validation = mock()
        errorModel = LoginErrorModel("", "")
        viewModel = LoginViewModel(validation, repo)
    }

    @Test
    fun testLoginSuccess() {
        // Given
        val response = AuthenticationInfo(1, AccountSetupNext.NO_NEXT_STEP, mapOf(), null)
        val observerMock = mock<Observer<Resource<AuthenticationInfo>>>()
        val validationResponse = LoginErrorModel("", "")

        viewModel.loginResponse.observeForever(observerMock)

        whenever(repo.login(any())) doReturn Observable.just(response)
        whenever(validation.validate(viewModel.loginData)) doReturn validationResponse
        viewModel.loginData.email = "email@email.com"
        viewModel.loginData.password = "password"

        // When
        viewModel.login()

        // Then
        verify(observerMock).onChanged(Resource.Loading())
        verify(observerMock).onChanged(Resource.Success(response))
    }

    @Test
    fun testKayboardAction() {
        // Given
        val response = AuthenticationInfo(1, AccountSetupNext.NO_NEXT_STEP, mapOf(), null)
        val validationResponse = LoginErrorModel("", "")

        whenever(repo.login(any())) doReturn Observable.just(response)
        whenever(validation.validate(viewModel.loginData)) doReturn validationResponse
        viewModel.loginData.email = "email@email.com"
        viewModel.loginData.password = "password"

        // When
        viewModel.login()

        // Then
        verify(repo, times(1)).login(any())
    }

    @Test
    fun testLoginError() {
        // Given
        val error = Error()
        val observerMock = mock<Observer<Resource<AuthenticationInfo>>>()
        val validationResponse = LoginErrorModel("", "")

        viewModel.loginResponse.observeForever(observerMock)

        whenever(repo.login(any())) doReturn Observable.error(error)
        whenever(validation.validate(viewModel.loginData)) doReturn validationResponse
        viewModel.loginData.email = "email"
        viewModel.loginData.password = "password"

        // When
        viewModel.login()

        // Then
        verify(observerMock).onChanged(Resource.Loading())
        verify(observerMock).onChanged(Resource.Error(error))
    }

    @Test
    fun testOnBackPressed() {
        // Given
        val observerMock = mock<Observer<Event>>()
        viewModel.events.observeForever(observerMock)

        // When
        viewModel.onBackPressed()

        // Then
        verify(observerMock).onChanged(Event.Exit)
    }

    @Test
    fun testForgotPassword() {
        // Given
        val observerMock = mock<Observer<Event>>()
        viewModel.events.observeForever(observerMock)

        // When
        viewModel.forgotPassword()

        // Then
        verify(observerMock).onChanged(Event.ForgotPassword)
    }
}

