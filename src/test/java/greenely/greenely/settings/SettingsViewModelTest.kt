@file:Suppress("KDocMissingDocumentation")

package greenely.greenely.settings

import android.app.Activity
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import android.content.Intent
import greenely.greenely.main.ui.MainActivity
import greenely.greenely.anyObject
import greenely.greenely.settings.data.Household
import greenely.greenely.settings.data.SettingsInfo
import greenely.greenely.settings.data.SettingsRepo
import greenely.greenely.settings.ui.SettingsViewModel
import greenely.greenely.settings.ui.UiEvent
import io.reactivex.Observable
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class SettingsViewModelTest {
    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    @Mock
    lateinit var repo: SettingsRepo

    @InjectMocks
    lateinit var viewModel: SettingsViewModel

    @Test
    fun testGetSettings_success() {
        val expectedSettingsInfo = SettingsInfo(true, "test@test.com")
        `when`(repo.getSettingsInfo()).thenReturn(Observable.just(expectedSettingsInfo))

        val loadingStates = mutableListOf<Boolean>()
        viewModel.isLoadingSettingsInfo().observeForever {
            it?.let {
                loadingStates += it
            }
        }

        assertThat(viewModel.getSettings().value).isEqualTo(expectedSettingsInfo)
        assertThat(loadingStates).isEqualTo(listOf(true, false))
    }

    @Test
    fun testGetSettings_cache() {
        val expectedSettingsInfo = SettingsInfo(true, "test@test.com")
        `when`(repo.getSettingsInfo()).thenReturn(Observable.just(expectedSettingsInfo))

        viewModel.getSettings()
        viewModel.getSettings()

        verify(repo, times(1)).getSettingsInfo()
    }

    @Test
    fun testGetSettings_error() {
        val error = Error()
        `when`(repo.getSettingsInfo()).thenReturn(Observable.error(error))

        val events = mutableListOf<UiEvent>()
        viewModel.getEvents().observeForever {
            it?.let { events += it }
        }

        viewModel.getSettings()

        assertThat(events.last()).isEqualTo(UiEvent.ShowError(error))

        viewModel.getSettings()

        verify(repo, times(2)).getSettingsInfo()
    }

    @Test
    fun testGetHousehold_success() {
        val household = Household(
                "Stockholm",
                "Not set",
                "145-150",
                "Villa",
                "Fjärrvärme",
                "Fjärrvärme",
                "Fjärrvärme",
                "Fjärrvärme",
                "2",
                "5",
                "1975"
        )

        `when`(repo.getHousehold()).thenReturn(
                Observable.just(
                        household
                )
        )

        val loadingStates = mutableListOf<Boolean>()
        viewModel.isLoadingHousehold().observeForever {
            it?.let { loadingStates += it }
        }

        assertThat(viewModel.getHousehold().value).isEqualTo(household)
        assertThat(loadingStates).isEqualTo(listOf(true, false))
    }

    @Test
    fun testGetHousehold_cache() {
        val household = Household(
                "Stockholm",
                "Not set",
                "145-150",
                "Villa",
                "Fjärrvärme",
                "Fjärrvärme",
                "Fjärrvärme",
                "Fjärrvärme",
                "2",
                "5",
                "1975"
        )

        `when`(repo.getHousehold()).thenReturn(
                Observable.just(
                        household
                )
        )

        viewModel.getHousehold()
        viewModel.getHousehold()

        verify(repo, times(1)).getHousehold()
    }

    @Test
    fun testGetHousehold_error() {
        val error = Error()
        `when`(repo.getHousehold()).thenReturn(Observable.error(error))

        val events = mutableListOf<UiEvent>()
        viewModel.getEvents().observeForever {
            it?.let { events += it }
        }

        viewModel.getHousehold()

        assertThat(events.last()).isEqualTo(UiEvent.ShowError(error))

        viewModel.getHousehold()

        verify(repo, times(2)).getHousehold()
    }

    @Test
    fun testChangePassword_success() {
        `when`(repo.changePassword(anyObject(), anyObject())).thenReturn(
                Observable.just(true)
        )

        val loadingStates = mutableListOf<Boolean>()
        viewModel.isLoadingChangePassword().observeForever {
            it?.let { loadingStates += it }
        }

        val events = mutableListOf<UiEvent>()
        viewModel.getEvents().observeForever {
            it?.let { events += it }
        }

        viewModel.changePassword("password", "new password")

        assertThat(loadingStates).isEqualTo(listOf(true, false))
        assertThat(events.last()).isEqualTo(UiEvent.PasswordChanged)
    }

    @Test
    fun testChangePassword_error() {
        val error = Error()

        `when`(repo.changePassword(anyObject(), anyObject())).thenReturn(
                Observable.error(error)
        )

        val events = mutableListOf<UiEvent>()
        viewModel.getEvents().observeForever { it?.let { events += it } }

        viewModel.changePassword("password", "new password")

        assertThat(events.last()).isEqualTo(UiEvent.ShowError(error))
    }

    @Test
    fun testOnActivityResult() {
        val requestCode = MainActivity.SIGN_POA_REQUEST
        val responseCode = Activity.RESULT_OK
        val intent = mock(Intent::class.java)

        `when`(repo.getHousehold()).thenReturn(Observable.empty())
        `when`(repo.getSettingsInfo()).thenReturn(Observable.empty())

        viewModel.onActivityResult(requestCode, responseCode, intent)

        verify(repo, times(1)).getHousehold()
        verify(repo, times(1)).getSettingsInfo()
    }
}

