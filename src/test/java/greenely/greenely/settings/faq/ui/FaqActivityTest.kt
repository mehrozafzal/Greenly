package greenely.greenely.settings.faq.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import greenely.greenely.TestApplication
import greenely.greenely.di.DaggerTestComponent
import greenely.greenely.settings.faq.ui.events.Event
import greenely.greenely.settings.faq.ui.models.FaqItem
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(
        sdk = [21],
        application = TestApplication::class,
        packageName = "greenely.greenely"
)
class FaqActivityTest {
    private lateinit var viewModel: FaqViewModel
    private lateinit var activity: FaqActivity

    private lateinit var events: MutableLiveData<Event>
    private lateinit var items: MutableLiveData<List<FaqItem>>

    @Before
    fun setUp() {
        events = MutableLiveData()
        items = MutableLiveData()

        viewModel = mock {
            on { this.events } doReturn events
            on { this.items } doReturn items
        }
        val viewModelFactory = mock<ViewModelProvider.Factory> {
            on { create(FaqViewModel::class.java) } doReturn viewModel
        }

        DaggerTestComponent.builder()
                .viewModelProviderFactory(viewModelFactory)
                .build()
                .inject(RuntimeEnvironment.application as TestApplication)

        activity = Robolectric.setupActivity(FaqActivity::class.java)
    }

    @Test
    fun testExit() {
        // When
        events.value = Event.Exit

        // Then
        assertThat(activity.isFinishing).isTrue()
    }

    @Test
    fun testSetItems() {
        // When
        items.value = listOf(FaqItem("Question", "Answer"))

        // Then
        assertThat(activity.binding.recyclerView.childCount).isEqualTo(1)
    }
}