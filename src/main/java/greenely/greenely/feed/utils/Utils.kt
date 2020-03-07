package greenely.greenely.feed.utils

import org.joda.time.format.DateTimeFormat
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*


fun String.getYearAndMonth() :String {

    val date = SimpleDateFormat("yyyy-MM-dd").parse(this)
    var formatter=DateTimeFormat.forPattern("MMMM yyyy").withLocale(Locale("sv", "SE"))
    return formatter.print(date.time)

}