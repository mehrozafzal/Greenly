package greenely.greenely.feed.utils

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import java.util.*

fun DateTime.getDateStr():String{
    val formatter: DateTimeFormatter = DateTimeFormat
            .forPattern("d MMMM yyyy")
            .withLocale(Locale("sv", "SE"))

    return formatter.print(this).split(" ").joinToString(" ") { it.capitalize() }
}