@file:Suppress("KDocMissingDocumentation")

package greenely.greenely.splash.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import greenely.greenely.accountsetup.models.AccountSetupNext
import greenely.greenely.models.AuthenticationInfo
import greenely.greenely.splash.data.SplashRepo
import greenely.greenely.splash.ui.navigation.SplashRoute
import greenely.greenely.store.UserStore
import io.reactivex.Observable
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SplashViewModelTest {

    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    private lateinit var repo: SplashRepo

    private lateinit var viewModel: SplashViewModel

    private lateinit var userStore: UserStore

    @Before
    fun setUp() {
        repo = mock()
        userStore = mock()
        viewModel = SplashViewModel(userStore, repo)
    }

    @Test
    fun testAutoLogin_onboarded() {
        // Given
        val response = AuthenticationInfo(1, AccountSetupNext.NO_NEXT_STEP, mapOf(), null)
        whenever(repo.checkAuth()) doReturn (Observable.just(response))

        val mockObserver = mock<Observer<AccountSetupNext>>()

        viewModel.accountSetupNext.observeForever(mockObserver)

        // When
        viewModel.autoLogin(false)

        verify(mockObserver).onChanged(AccountSetupNext.NO_NEXT_STEP)
        assertThat(viewModel.finishSplashDuration.value == true)
    }

    @Test
    fun testAutoLogin_notOnboarded() {
        // Given
        val response = AuthenticationInfo(1, AccountSetupNext.VERIFICATION, mapOf(), null)
        whenever(repo.checkAuth()) doReturn Observable.just(response)

        // When
        viewModel.autoLogin(false)

        // Then
        assertThat(viewModel.finishSplashDuration.value == false)
    }

    @Test
    fun testAutoLogin_error() {
        // Given
        val error = Error()
        whenever(repo.checkAuth()).thenReturn(Observable.error(error))

        // When
        viewModel.autoLogin(false)

        // Then
        assertThat(viewModel.finishSplashDuration.value == false)
    }

    @Test
    fun testLogin() {
        // Given
        val mockObserver = mock<Observer<SplashRoute>>()
        viewModel.navigate.observeForever(mockObserver)

        // When
        viewModel.onClickLogin()

        // Then
        verify(mockObserver).onChanged(SplashRoute.LOGIN)
    }

    @Test
    fun testRegister() {
        // Given
        val mockObserver = mock<Observer<SplashRoute>>()
        viewModel.navigate.observeForever(mockObserver)

        // When
        viewModel.onClickRegister()

        // Then
        verify(mockObserver).onChanged(SplashRoute.REGISTER)
    }
}

