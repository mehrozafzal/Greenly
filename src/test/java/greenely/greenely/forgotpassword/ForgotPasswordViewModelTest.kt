@file:Suppress("KDocMissingDocumentation")

package greenely.greenely.forgotpassword

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import android.view.inputmethod.EditorInfo
import greenely.greenely.R
import io.reactivex.Completable
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.anyString
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class ForgotPasswordViewModelTest {

    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    @Mock
    lateinit var repo: ForgotPasswordRepo

    @Mock
    lateinit var validator: InputValidation

    @InjectMocks
    lateinit var viewModel: ForgotPasswordViewModel

    @Before
    fun setup(){
        validator = InputValidation(Application())
        viewModel = ForgotPasswordViewModel(repo,validator)
    }

    @Test
    fun testSendEmail_success() {
        `when`(repo.sendEmail(anyString())).thenReturn(Completable.complete())

        var lastNonNullEvent: ForgotPasswordEvent? = null
        viewModel.events.observeForever {
            it?.let {
                lastNonNullEvent = it
            }
        }

        viewModel.sendEmail()

        assertThat(lastNonNullEvent).isEqualTo(ForgotPasswordEvent.SHOW_SUCCESS)
    }

    @Test
    fun testSendEmail_keyboardAction() {
        `when`(repo.sendEmail(anyString())).thenReturn(Completable.complete())

        var lastNonNullEvent: ForgotPasswordEvent? = null
        viewModel.events.observeForever {
            it?.let {
                lastNonNullEvent = it
            }
        }

        viewModel.handleEditorAction(EditorInfo.IME_ACTION_DONE)

        assertThat(lastNonNullEvent).isEqualTo(ForgotPasswordEvent.SHOW_SUCCESS)
    }

    @Test
    fun testSendEmail_error() {
        val error = Error()
        `when`(repo.sendEmail(anyString())).thenReturn(Completable.error(error))

        var lastNonNullError: Throwable? = null
        viewModel.error.observeForever {
            it?.let {
                lastNonNullError = it
            }
        }

        viewModel.sendEmail()

        assertThat(lastNonNullError).isEqualTo(error)
    }

    @Test
    fun testNavigation_help() {
        var lastNonNullEvent: ForgotPasswordEvent? = null
        viewModel.events.observeForever {
            it?.let {
                lastNonNullEvent = it
            }
        }

        viewModel.onMenuItemClicked(R.id.help)

        assertThat(lastNonNullEvent).isEqualTo(ForgotPasswordEvent.SHOW_HELP)
    }

    @Test
    fun testNavigation_back() {
        var lastNonNullEvent: ForgotPasswordEvent? = null
        viewModel.events.observeForever {
            it?.let {
                lastNonNullEvent = it
            }
        }

        viewModel.onBackPressed()

        assertThat(lastNonNullEvent).isEqualTo(ForgotPasswordEvent.FINISH)
    }
}

