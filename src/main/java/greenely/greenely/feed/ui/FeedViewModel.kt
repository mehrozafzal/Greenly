package greenely.greenely.feed.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import greenely.greenely.OpenClassOnDebug
import greenely.greenely.feed.data.FeedRepo
import greenely.greenely.feed.models.feeditems.DatedFeedItem
import greenely.greenely.feed.utils.FeedItemSorter
import greenely.greenely.feed.utils.FeedLabelInserter
import greenely.greenely.models.Resource
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject

/**
 * Loadable resource representation of a feedItems item list.
 */
typealias FeedResource = Resource<List<Any>>

/**
 * View model for the feedItems view.
 */
@OpenClassOnDebug
class FeedViewModel @Inject constructor(
        private val repo: FeedRepo,
        private val feedItemSorter: FeedItemSorter,
        private val feedLabelInserter: FeedLabelInserter
) : ViewModel() {

    private val _feed = MutableLiveData<FeedResource>()
    val feed: LiveData<FeedResource>
        get() {
            _feed.value.let {
                if (it == null) fetchFeed()
                else if (it is Resource.Success<List<Any>>) {
                    val newFeedItems = it.value
                            .filterIsInstance(DatedFeedItem::class.java)
                            .map { it.read() }
                    _feed.value = Resource.Success(feedLabelInserter.insertLabels(newFeedItems))
                }
            }

            return _feed
        }

    /**
     * Refresh the feedItems.
     */
    fun refresh() {
        fetchFeed()
    }

    private fun fetchFeed() {
        repo.getFeed()
                .doOnSubscribe { if (_feed.value == null) _feed.value = Resource.Loading() }
                .subscribeBy(
                        onNext = {
                            _feed.value = Resource.Success(
                                    feedLabelInserter.insertLabels(feedItemSorter.sort(it))
                            )
                        },
                        onError = {
                            _feed.value = Resource.Error(it)
                        }
                )
    }
}
