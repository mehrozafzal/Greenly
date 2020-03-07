package greenely.greenely.history

import android.util.Log
import com.squareup.moshi.Json
import greenely.greenely.OpenClassOnDebug
import greenely.greenely.api.GreenelyApi
import greenely.greenely.store.UserStore
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Data that can be used for navigation in history.
 */
data class NavigationDataPoint(
        @field:Json(name = "timestamp") val timeStamp: Long,
        @field:Json(name = "usage") val usageWh: Int?,
        val selectable: Boolean,
        val hasDetailedData: Boolean,
        val monthTimeStamp: Long?

) {
    /**
     * [timeStamp] as a [DateTime].
     */
    val dateTime: DateTime
        get() = DateTime(timeStamp * 1000).withZone(DateTimeZone.UTC)

    /**
     * Get the [usageWh] in kWh
     */
    val usage: Float?
        get() = usageWh?.toFloat()?.div(1000)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NavigationDataPoint

        if (timeStamp != other.timeStamp) return false

        return true
    }

    override fun hashCode(): Int {
        return timeStamp.hashCode()
    }


}

/**
 * A year and its data.
 */
data class Year(
        val year: Int,
        val months: List<NavigationDataPoint>
)

/**
 * The response from the year endpoint.
 */
data class YearResponse(
        val state: Int,
        val years: List<Year>,
        @field:Json(name = "components")
        private val componentStrings: List<String>,
        val maxValue: Int

) {
    /**
     * Get the components that should be displayed.
     */
    val components: List<HistoryComponent>
        get() = componentStrings.map { HistoryComponent.fromString(it) }.filterNotNull()
}

/**
 * model representing a single week of navigation data.
 */
data class Week(
        val weekNumber: Int,
        val days: List<NavigationDataPoint>
)

/**
 * The response from the week endpoint.
 */
data class WeekResponse(
        val weeks: List<Week>,
        @field:Json(name = "components") private val componentStrings: List<String>,
        val maxValue: Int
) {
    /**
     * Get the components that should be displayed.
     */
    val components: List<HistoryComponent>
        get() = componentStrings.map { HistoryComponent.fromString(it) }.filterNotNull()
}

/**
 * Generalized model of navigation data.
 */
data class NavigationData(
        val dateNumber: Int,
        val maxValue: Int,
        val data: List<NavigationDataPoint>
) {
    /**
     * Get the maximum consumption.
     */
    val max: Float
        get() = maxValue.toFloat().div(1000.0f)
}

/**
 * Model of distribution of consumption.
 */
data class DistributionDataPoint(
        val start: String,
        val end: String,
        @field:Json(name = "percentage") private val centOfPercentage: Int
) {
    /**
     * Get the percentage used for the time period.
     */
    val percentage: Int
        get() = Math.round(centOfPercentage.toFloat() / 100.0f)
}

/**
 * The different states of the history view.
 */
enum class HistoryState {
    /**
     * If the history view has data.
     */
    HAS_DATA,

    AWATING_DATA,

    /**
     * If the history view needs PoA to be signed.
     */
    NEEDS_POA;

    companion object {
        /**
         * Create a [HistoryState] from [int].
         */
        fun fromInt(int: Int): HistoryState = when (int) {
            1 -> NEEDS_POA
            2 -> AWATING_DATA
            else -> HAS_DATA
        }
    }
}

/**
 * Model class of consumption.
 */
data class Consumption(
        val timestamp: Long,
        @field:Json(name = "usage") val usageWh: Int?
) {
    /**
     * Get the date of the consumption.
     */
    val dateTime: DateTime
        get() = DateTime(timestamp * 1000).withZone(DateTimeZone.UTC)

    /**
     * Get the usage in kWh
     */
    val usage: Float?
        get() = usageWh?.toFloat()?.div(1000)
}

/**
 * Data point representing the min max chart.
 */
data class MinMaxDataPoint(
        private val timestamp: Long,
        val minimum: Int?,
        val maximum: Int?,
        @field:Json(name = "usage") private val usageWh: Int?
) {
    /**
     * The date time of the point.
     */
    val dateTime: DateTime
        get() = DateTime(timestamp * 1000).withZone(DateTimeZone.UTC)

    /**
     * The usage in kWh
     */
    val usage
        get() = usageWh?.toFloat()?.div(1000)
}

/**
 * The response of the min max request.
 */
data class MinMaxResponse(
        val points: List<MinMaxDataPoint>,
        private val maxValue: Int?
) {
    /**
     * The maximum value.
     */
    val max
        get() = maxValue?.toFloat()?.div(1000.0f)
}

/**
 * RankResponse from usage request.
 */
data class UsageResponse(
        val points: List<Consumption>,
        private val maxValue: Int?
) {
    /**
     * The maximum consumption value.
     */
    val max: Float?
        get() = maxValue?.toFloat()?.div(1000.0f)
}

/**
 * Wrapper class for temperature response
 */
data class TemperatureResponse(
        val points: List<Temperature>,
        private val maxTemperatureValue: Int?,
        private val minTemperatureValue: Int?,
        private val maxUsageValue: Int?
) {
    /**
     * The maximum temperature.
     */
    val temperatureMax
        get() = maxTemperatureValue?.toFloat()?.div(100.0f)

    /**
     * The minimum temperature.
     */
    val temperatureMin
        get() = minTemperatureValue?.toFloat()?.div(100.0f)

    /**
     * The maximum consumption.
     */
    val usageMax
        get() = maxUsageValue?.toFloat()?.div(1000.0f)
}


/**
 * Wrapper class for Price response
 */
data class PriceResponse(
        val points: List<Price>,
        private val maxPriceValue: Int?,
        private val minPriceValue: Int?,
        private val maxUsageValue: Int?
) {
    /**
     * The maximum temperature.
     */
    val priceMax
        get() = maxPriceValue?.toFloat()?.div(100.0f)

    /**
     * The minimum temperature.
     */
    val priceMin
        get() = minPriceValue?.toFloat()?.div(100.0f)

    /**
     * The maximum consumption.
     */
    val usageMax
        get() = maxUsageValue?.toFloat()?.div(1000.0f)
}

/**
 * Model combining temperature and consumption.
 */
data class Temperature(
        val timestamp: Long,
        @field:Json(name = "usage") val usageWh: Int?,
        @field:Json(name = "temperature") val temperatureCents: Int?
) {
    /**
     *  Get the date of the temperature data.
     */
    val dateTime
        get() = DateTime(timestamp * 1000).withZone(DateTimeZone.UTC)

    /**
     * Get the usage in kWh
     */
    val usage: Float?
        get() = usageWh?.toFloat()?.div(1000)

    /**
     * Get the temperature in celcius.
     */
    val temperature: Float?
        get() = temperatureCents?.toFloat()?.div(100.0f)
}

/**
 * Model combining price and consumption.
 */
data class Price(
        val timestamp: Long,
        @field:Json(name = "usage") val usageWh: Int?,
        @field:Json(name = "nordPoolSpotPrice") val nordPoolSpotPrice: Int?
) {
    /**
     *  Get the date of the temperature data.
     */
    val dateTime
        get() = DateTime(timestamp * 1000).withZone(DateTimeZone.UTC)

    /**
     * Get the usage in kWh
     */
    val usage: Float?
        get() = usageWh?.toFloat()?.div(1000)

    /**
     * Get the temperature in celcius.
     */
    val price: Float?
        get() = nordPoolSpotPrice?.toFloat()?.div(100.0f)
}

/**
 * The resolution of the history.
 */
sealed class HistoryResolution {
    /**
     * Yearly resolution, displaying days of a month in detailed view.
     */
    object Year : HistoryResolution()

    /**
     * Weekly resolution showing the days of the week in detailed view.
     */
    data class Month(val month: DateTime?) : HistoryResolution()
}

/**
 * Enum class for history components.
 */
enum class HistoryComponent {
    /**
     * A usage component.
     */
    USAGE,
    /**
     * A temperature component.
     */
    TEMPERATURE,
    /**
     * A distribution component.
     */
    DISTRIBUTION,
    /**
     * A min max component.
     */
    MIN_MAX;

    companion object {
        /**
         * Create a component from [string].
         *
         * @param string String representation of a component (not the same as name).
         */
        fun fromString(string: String): HistoryComponent? =
                when (string) {
                    "usage" -> USAGE
                    "temperature" -> TEMPERATURE
                    "distribution" -> DISTRIBUTION
                    "min_max" -> MIN_MAX
                    else -> null
                }
    }
}

/**
 * Model for the initial information used by the history.
 */
data class HistoryResponse(
        val state: HistoryState,
        val navigationData: List<NavigationData>,
        val components: List<HistoryComponent>
)

/**
 * [HistoryRepo] using the API as the model layer.
 */
@OpenClassOnDebug
class HistoryRepo(
        private val api: GreenelyApi,
        private val userStore: UserStore,
        var resolution: HistoryResolution
) {

    private val yearResponse: Observable<YearResponse>
        get() = api.getYear("JWT ${userStore.token}")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext { userStore.token = it.jwt }
                .doOnError { Log.d(HistoryRepo::class.java.name, "Error fetching", it) }
                .map { it.data }

    private val weekResponse: Observable<WeekResponse>
        get() = (resolution as? HistoryResolution.Month)?.month?.let {
            api.getWeeks("JWT ${userStore.token}", it.year, it.monthOfYear)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext { userStore.token = it.jwt }
                    .doOnError { Log.d(HistoryRepo::class.java.name, "Error fetching", it) }
                    .map { it.data }
        } ?: Observable.empty()

    fun getHistory(): Observable<HistoryResponse> =
            if (resolution is HistoryResolution.Year) {
                yearResponse.map { resp ->
                    HistoryResponse(
                            HistoryState.fromInt(resp.state),
                            resp.years.map { NavigationData(it.year, resp.maxValue, it.months) },
                            resp.components
                    )
                }
            } else {
                weekResponse.map { resp ->
                    HistoryResponse(
                            HistoryState.HAS_DATA,
                            resp.weeks.map { NavigationData(it.weekNumber, resp.maxValue, it.days) },
                            resp.components
                    )
                }
            }


    fun getUsage(date: DateTime): Observable<UsageResponse> =
            if (resolution is HistoryResolution.Year) {
                api.getMonthUsage("JWT ${userStore.token}", date.year, date.monthOfYear)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnNext { userStore.token = it.jwt }
                        .map { it.data }
            } else {
                api.getDayUsage("JWT ${userStore.token}", date.year, date.monthOfYear, date.dayOfMonth)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnNext { userStore.token = it.jwt }
                        .map { it.data }
            }

    fun getTemperature(date: DateTime): Observable<TemperatureResponse> =
            if (resolution is HistoryResolution.Year) {
                api.getMonthTemperature("JWT ${userStore.token}", date.year, date.monthOfYear)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnNext { userStore.token = it.jwt }
                        .map { it.data }
            } else {
                api.getDayTemperature("JWT ${userStore.token}", date.year, date.monthOfYear, date.dayOfMonth)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnNext { userStore.token = it.jwt }
                        .map { it.data }
            }

    fun getPrice(date: DateTime): Observable<PriceResponse> =
            if (resolution is HistoryResolution.Year) {
                api.getMonthPrice("JWT ${userStore.token}", date.year, date.monthOfYear)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnNext { userStore.token = it.jwt }
                        .map { it.data }
            } else {
                api.getDayPrice("JWT ${userStore.token}", date.year, date.monthOfYear, date.dayOfMonth)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnNext { userStore.token = it.jwt }
                        .map { it.data }
            }


    fun getDistribution(date: DateTime): Observable<List<DistributionDataPoint>> =
            if (resolution is HistoryResolution.Year) {
                api.getMonthDistribution("JWT ${userStore.token}", date.year, date.monthOfYear)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnNext { userStore.token = it.jwt }
                        .map { it.data }
            } else {
                api.getDayDistribution("JWT ${userStore.token}", date.year, date.monthOfYear, date.dayOfMonth)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnNext { userStore.token = it.jwt }
                        .map { it.data }
            }

    fun getMinMax(date: DateTime): Observable<MinMaxResponse> =
            if (resolution is HistoryResolution.Year) {
                api.getMonthMinMax("JWT ${userStore.token}", date.year, date.monthOfYear)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnNext { userStore.token = it.jwt }
                        .map { it.data }
            } else {
                api.getDayMinMax("JWT ${userStore.token}", date.year, date.monthOfYear, date.dayOfMonth)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnNext { userStore.token = it.jwt }
                        .map { it.data }
            }
}

@OpenClassOnDebug
@Singleton
class HistoryRepoFactory @Inject constructor(
        private val api: GreenelyApi,
        private val userStore: UserStore
) {

    private val cachedRepos = mutableMapOf<HistoryResolution, HistoryRepo>()

    fun create(resolution: HistoryResolution): HistoryRepo {
        if (cachedRepos[resolution] == null) {
            cachedRepos.put(resolution, HistoryRepo(api, userStore, resolution))
        }
        return cachedRepos[resolution]!!
    }
}
