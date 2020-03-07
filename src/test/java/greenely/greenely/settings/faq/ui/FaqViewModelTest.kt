package greenely.greenely.settings.faq.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import greenely.greenely.settings.faq.data.FaqRepo
import greenely.greenely.settings.faq.ui.events.Event
import greenely.greenely.settings.faq.ui.models.FaqItem
import io.reactivex.Observable
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class FaqViewModelTest {
    @JvmField
    @Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var repo: FaqRepo
    private lateinit var viewModel: FaqViewModel

    private val expectedFaqItems = listOf(FaqItem("Question", "Answer"))

    @Before
    fun setUp() {
        repo = mock {
            on { getFaqItems() } doReturn Observable.just(expectedFaqItems)
        }

        viewModel = FaqViewModel(repo)
    }

    @Test
    fun testFetch() {
        // Given
        val mockObserver: Observer<List<FaqItem>> = mock()
        viewModel.items.observeForever(mockObserver)

        // When
        viewModel.fetch()

        // Then
        verify(mockObserver).onChanged(expectedFaqItems)
    }

    @Test
    fun testExit() {
        // Given
        val mockObserver: Observer<Event> = mock()
        viewModel.events.observeForever(mockObserver)

        // When
        viewModel.exit()

        // Then
        verify(mockObserver).onChanged(Event.Exit)
    }
}
