package greenely.greenely.feed.utils

import greenely.greenely.OpenClassOnDebug
import greenely.greenely.feed.models.feeditems.CostAnalysisFeedItem
import greenely.greenely.feed.models.feeditems.DatedFeedItem
import javax.inject.Inject

@OpenClassOnDebug
class FeedItemSorter @Inject constructor() {
    fun sort(feedItems: List<DatedFeedItem>): List<DatedFeedItem> {
        return feedItems.sortedBy { it.date }.reversed()
    }
}

