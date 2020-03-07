package greenely.greenely.feed.models

import org.joda.time.DateTime

open class DataPoint(
        val date: DateTime,
        val usage: Int?
) {
    fun asPair(): Pair<DateTime, Int> = date to (usage ?: 0)
}

