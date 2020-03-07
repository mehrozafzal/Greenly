package greenely.greenely.feed.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.nhaarman.mockito_kotlin.*
import greenely.greenely.feed.data.FeedRepo
import greenely.greenely.feed.models.feeditems.DatedFeedItem
import greenely.greenely.feed.utils.FeedItemSorter
import greenely.greenely.feed.utils.FeedLabelInserter
import greenely.greenely.models.Resource
import io.reactivex.Observable
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FeedViewModelTest {
    @JvmField
    @Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var repo: FeedRepo
    private lateinit var itemSorter: FeedItemSorter
    private lateinit var labelInserter: FeedLabelInserter

    private lateinit var viewModel: FeedViewModel

    @Before
    fun setUp() {
        repo = mock()
        itemSorter = mock {
            on { sort(any()) } doReturn listOf<DatedFeedItem>()
        }
        labelInserter = mock {
            on { insertLabels(any()) } doReturn listOf<Any>()
        }

        viewModel = FeedViewModel(repo, itemSorter, labelInserter)
    }

    @Test
    fun testFeed() {
        // Given
        val feedItemList: List<DatedFeedItem> = listOf()
        repo.stub {
            on { getFeed() } doReturn Observable.just(feedItemList)
        }

        // When
        val mockObserver: Observer<FeedResource> = mock()
        viewModel.feed.observeForever(mockObserver)

        // Then
        verify(itemSorter).sort(feedItemList)
        verify(labelInserter).insertLabels(listOf())

        verify(mockObserver, times(1)).onChanged(Resource.Success(listOf()))
    }

    @Test
    fun testRefresh() {
        // Given
        val feedItemList: List<DatedFeedItem> = listOf()
        repo.stub {
            on { getFeed() } doReturn Observable.just(feedItemList)
        }
        val mockObserver: Observer<FeedResource> = mock()
        viewModel.feed.observeForever(mockObserver)

        // When
        viewModel.refresh()

        // Then
        verify(itemSorter, times(2)).sort(feedItemList)
        verify(labelInserter, times(2)).insertLabels(listOf())

        verify(mockObserver, times(2)).onChanged(Resource.Success(listOf()))
    }
}