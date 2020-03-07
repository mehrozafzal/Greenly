@file:Suppress("KDocMissingDocumentation")

package greenely.greenely.splash.ui

import androidx.lifecycle.MutableLiveData
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import greenely.greenely.TestApplication
import greenely.greenely.accountsetup.models.AccountSetupNext
import greenely.greenely.di.DaggerTestComponent
import greenely.greenely.splash.ui.navigation.SplashRoute
import greenely.greenely.tracking.Tracker
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(
        sdk = [21],
        packageName = "greenely.greenely",
        application = TestApplication::class
)
class SplashActivityTest {
    private lateinit var activity: SplashActivity
    private lateinit var viewModel: SplashViewModel
    private val navigation = MutableLiveData<SplashRoute>()
    private val identification = MutableLiveData<Tracker.UserIdentifier>()
    private val accountSetupNext = MutableLiveData<AccountSetupNext>()
    private val finishSplashDuration = MutableLiveData<Boolean>()

    @Before
    fun setUp() {
        viewModel = mock {
            on { this.navigate } doReturn navigation
            on { this.identification } doReturn identification
            on { this.accountSetupNext } doReturn accountSetupNext
            on { this.finishSplashDuration } doReturn finishSplashDuration

        }

        DaggerTestComponent.builder().viewModelProviderFactory(mock {
            on { create(SplashViewModel::class.java) } doReturn viewModel
        }).build().inject(RuntimeEnvironment.application as TestApplication)

        activity = Robolectric.setupActivity(SplashActivity::class.java)
    }

    @Ignore
    @Test
    fun testSetUp() {
        assertThat(activity).isNotNull()
        verify(activity.tracker).trackScreen("Splash")
    }

}

