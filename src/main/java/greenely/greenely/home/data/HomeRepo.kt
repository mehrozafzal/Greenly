package greenely.greenely.home.data

import com.squareup.moshi.FromJson
import com.squareup.moshi.Json
import com.squareup.moshi.ToJson
import greenely.greenely.OpenClassOnDebug
import greenely.greenely.R
import greenely.greenely.api.GreenelyApi
import greenely.greenely.home.models.FacilitiesResponse
import greenely.greenely.home.models.HomeResponse
import greenely.greenely.home.models.HomeResponseJson
import greenely.greenely.store.UserStore
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@OpenClassOnDebug
data class Comparison(
        @field:Json(name = "timestamp") val timestamp: Long,
        @field:Json(name = "me") private val meWh: Int?,
        @field:Json(name = "others") private val othersWh: Int?,
        @field:Json(name = "best") private val bestWh: Int?
) {

    val me: Float?
        get() = formatValue(meWh)

    val others: Float?
        get() = formatValue(othersWh)

    val best: Float?
        get() = formatValue(bestWh)

    val monthName: String?
        get() = monthFormatter.print(date)

    val weekNumber: String?
        get() = weekFormatter.print(date).toString()

    val day: String?
        get() = dayFormatter.print(date)

    val date: DateTime?
        get() = timestamp.let { DateTime(it.times(1000)) }

    companion object {
        val monthFormatter: DateTimeFormatter = DateTimeFormat.forPattern("MMMM")
                .withLocale(Locale("sv", "SE"))

        val weekFormatter: DateTimeFormatter = DateTimeFormat.forPattern("ww")

        val dayMonthFormatter: DateTimeFormatter = DateTimeFormat.forPattern("d MMM").withLocale(Locale("sv", "SE"))


        val dayFormatter: DateTimeFormatter = DateTimeFormat.forPattern("EEEE dd MMMM")
                .withLocale(Locale("sv", "SE"))
    }

    private fun formatValue(value: Int?): Float? {
        return value?.div(1000.0)?.toFloat()
    }


}

class HomeResponseAdapter {
    @FromJson
    fun homeResponseFromJson(json: HomeResponseJson): HomeResponse =
            HomeResponse(
                    state = json.state,
                    comparison = json.points.maxBy { point -> point.timestamp }!!,
                    comparisonInfoTitle = json.comparisonInfoTitle,
                    comparisonInfoText = json.comparisonInfoText,
                    feedback = json.feedback,
                    points = json.points,
                    resolution = json.resolution,
                    pointsMaxValue = json.pointsMaxValue?.div(1000.0f),
                    comparisonMaxValue = json.comparisonMaxValue?.div(1000.0f),
                    consumptionPercentage = json.consumptionPercentage,
                    consumptionDescription = json.consumptionDescription,
                    consumptionDifference = json.consumptionDifference,
                    description = json.description,
                    numFriends = json.num_friends
            )
}

class ResolutionAdapter {

    fun resolutionToRequest(value: Int): String = when (value) {
        R.id.days -> "D"
        R.id.weeks -> "W"
        R.id.months -> "M"
        else -> throw IllegalArgumentException()
    }

}


enum class DataState {
    NEEDS_POA,
    WAITING,
    HAS_DATA,
    WAITING_FOR_ZAVANNE,
    ZAVANNE_ERROR
}

class DataStateAdapter {
    @FromJson
    fun fromJson(json: Int): DataState = when (json) {
        1 -> DataState.NEEDS_POA
        2 -> DataState.WAITING
        3 -> DataState.HAS_DATA
        4 -> DataState.WAITING_FOR_ZAVANNE
        5 -> DataState.ZAVANNE_ERROR
        else -> null
    }!!

    @ToJson
    fun toJson(@Suppress("UNUSED_PARAMETER") state: DataState): Int = throw UnsupportedOperationException()
}

@OpenClassOnDebug
@Singleton
class HomeRepo @Inject constructor(
        private val api: GreenelyApi,
        private val userStore: UserStore
) {

    fun getHome(resolution: Int): Observable<HomeResponse> =
            api.getHome("JWT ${userStore.token}", ResolutionAdapter().resolutionToRequest(resolution))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext { userStore.token = it.jwt }
                    .map { HomeResponseAdapter().homeResponseFromJson(it.data) }


    fun getFacilities(): Observable<FacilitiesResponse> =
            api.getFacilities("JWT ${userStore.token}")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext { userStore.token = it.jwt }
                    .map { it.data }

    //Todo add new api call back

}