package greenely.greenely.feed.models.feeditems

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import java.util.*

data class DateLabel(
        private val date: DateTime
) {
    companion object {
        val formatter: DateTimeFormatter = DateTimeFormat
                .forPattern("EEEE d MMMM YYYY")
                .withLocale(Locale("sv", "SE"))
    }

    val label: String = formatter.print(date).capitalize()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DateLabel

        if (date != other.date) return false

        return true
    }

    override fun hashCode(): Int {
        var result = date.hashCode()
        result = 31 * result + label.hashCode()
        return result
    }


}

