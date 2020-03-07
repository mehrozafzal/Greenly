package greenely.greenely.feed.utils

import android.text.format.DateUtils
import org.joda.time.DateTime
import javax.inject.Inject

class TimeAgoStrategy @Inject constructor() {
    fun getTimeAgoText(date: DateTime): String {
        return DateUtils.getRelativeTimeSpanString(
                date.millis, DateTime.now().millis, DateUtils.MINUTE_IN_MILLIS
        ).toString().replace("\"", "")
    }
}

