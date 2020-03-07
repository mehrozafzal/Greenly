package greenely.greenely.feed.utils

import greenely.greenely.OpenClassOnDebug
import greenely.greenely.feed.models.feeditems.DateLabel
import greenely.greenely.feed.models.feeditems.DatedFeedItem
import greenely.greenely.feed.models.feeditems.NoMoreItems
import javax.inject.Inject

@OpenClassOnDebug
class FeedLabelInserter @Inject constructor() {
    fun insertLabels(feedItems: List<DatedFeedItem>): List<Any> {
        val newList = mutableListOf<Any>()
        for (i in (0 until feedItems.size)) {
            val previousDate = feedItems.getOrNull(i - 1)?.date
            val currentItem = feedItems[i]

            if (previousDate?.year != currentItem.date.year || previousDate.dayOfYear != currentItem.date.dayOfYear) {
                newList.add(DateLabel(currentItem.date))
            }
            newList.add(currentItem)
        }

        newList.add(NoMoreItems)

        return newList
    }
}

