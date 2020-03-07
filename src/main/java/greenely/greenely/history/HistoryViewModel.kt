package greenely.greenely.history

import android.annotation.SuppressLint
import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.content.Intent
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.annotation.VisibleForTesting
import androidx.databinding.ObservableInt
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import dagger.multibindings.ElementsIntoSet
import greenely.greenely.main.ui.MainActivity
import greenely.greenely.OpenClassOnDebug
import greenely.greenely.R
import greenely.greenely.retail.util.RetailStateHandler
import greenely.greenely.signature.ui.SignatureActivity
import greenely.greenely.utils.SingleLiveEvent
import greenely.greenely.utils.consumptionToString
import greenely.greenely.utils.formatDate
import io.reactivex.rxkotlin.subscribeBy
import org.joda.time.DateTime
import org.joda.time.DateTimeConstants
import org.joda.time.format.DateTimeFormat
import java.util.*
import javax.inject.Inject
import kotlin.math.roundToInt

/**
 * Events to be propagated to the UI.
 */
sealed class UiEvent {

    /**
     * Start a new activity showing more detailed data to the user.
     */
    data class ShowMore(val historyResolution: HistoryResolution) : UiEvent()

    /**
     * Notify the UI to show an error.
     */
    data class ShowError(val error: Throwable) : UiEvent()

    /**
     * Notify the UI to show an error.
     */
    data class ShowRetail(val historyResolution: HistoryResolution?) : UiEvent()

    /**
     * When the UI should start a new activity.
     */
    data class StartActivity(
            val activity: Class<out Activity>, val requestCode: Int? = null
    ) : UiEvent()
}

/**
 * View model for handling the functionality in history.
 */
@OpenClassOnDebug
class HistoryViewModel @Inject constructor(
        val factory: HistoryRepoFactory,
        val retailStateHandler: RetailStateHandler
) : ViewModel() {

    private var selectedDataPoint = MutableLiveData<NavigationDataPoint>()
    private var previousSelectedPoint = MutableLiveData<NavigationDataPoint>()


    private val loading = MutableLiveData<Boolean>()
    private val minMaxLoading = MutableLiveData<Boolean>()
    private val distributionLoading = MutableLiveData<Boolean>()
    private val navigationPageIndex = MutableLiveData<Int>()
    private val components = MutableLiveData<List<HistoryComponent>>()
    private val consumptionLoading = MutableLiveData<Boolean>()
    private val temperatureLoading = MutableLiveData<Boolean>()
    private val priceLoading = MutableLiveData<Boolean>()
    private val events = SingleLiveEvent<UiEvent>()

    var history = MutableLiveData<HistoryResponse>()
    private val consumption = mutableMapOf<DateTime, MutableLiveData<UsageResponse>>()
    private val temperature = mutableMapOf<DateTime, MutableLiveData<TemperatureResponse>>()
    private val price = mutableMapOf<DateTime, MutableLiveData<PriceResponse>>()

    private val distribution = mutableMapOf<DateTime, MutableLiveData<List<DistributionDataPoint>>>()
    private val minMax = mutableMapOf<DateTime, MutableLiveData<MinMaxResponse>>()

    val selectedChart: ObservableInt = ObservableInt(R.id.totalChart)


    /**
     * The title of the toolbar, null for default.
     */
    val toolbarTitle = ObservableField<String?>()

    /**
     * The title for the no content view.
     */
    val noContentTitle = ObservableField<Int?>()

    /**
     * The body of the no content view.
     */
    val noContentBody = ObservableField<Int?>()

    /**
     * If the sign PoA button is visible in the no content view.
     */
    val isPoaMissing = ObservableBoolean(false)

    val isDataAvailable = ObservableBoolean(false)


    /**
     * If the view can go deeper to a new resolution.
     */
    val canShowMore = ObservableBoolean(false)


    /**
     * Get the total consumption.
     */
    val consumptionTotal = ObservableField<String>("--")

    /**
     * Value for consumption.
     */
    val consumptionDescription = ObservableField<String>("")

    /**
     * Get the resolution of the underlying repo.
     */
    var resolution = ObservableField<HistoryResolution>()

    var selectedResolution = ObservableInt()


    private lateinit var repo: HistoryRepo

    /**
     * Initialize the repo. Only visible in testing.
     */
    @VisibleForTesting
    fun initializeRepo(resolution: HistoryResolution?) {
        if (this.resolution.get() == null) {
            if (resolution != null) {
                this.resolution.set(resolution)
            } else {
                this.resolution.set(HistoryResolution.Year)
            }
        }


        this.resolution.get()?.let {
            repo = factory.create(it)
            repo.resolution = it
        }

        setSelectedYear()

    }

    /**
     * Get the history data for a specified [resolution].
     */
    @SuppressLint("VisibleForTests")
    fun getHistory(resolution: HistoryResolution): LiveData<HistoryResponse> {
        initializeRepo(resolution)

        history.let {
            if (it.value == null || !isDataAvailable.get() ) {
                fetchHistory()
            }

            return it
        }
    }

    /**
     * Select a specific [dataPoint].
     */
    fun selectDataPoint(dataPoint: NavigationDataPoint) {
        selectedDataPoint.value = dataPoint

        consumptionTotal.set(dataPoint.usage?.consumptionToString() ?: "--")
        consumptionDescription.set(createDescriptionLabel(dataPoint.dateTime))
        val hasContent = (dataPoint.hasDetailedData)

        if (hasContent) {
            // Disable going to a deeper resolution if resolution is already at the deepest level.
            if (repo.resolution !is HistoryResolution.Month) canShowMore.set(hasContent)
        } else if (!isPoaMissing.get()) {
            noContentTitle.set(R.string.no_content_title)
            noContentBody.set(R.string.no_content_body)
        }
    }

    /**
     * Update the route index to be [index].
     *
     * @param index The index of the route.
     */
    fun updatePageIndex(index: Int) {
        navigationPageIndex.value = index
    }

    /**
     * Notify the UI that the user want to go one resolution deeper.
     */
    fun showMore() {
        if (repo.resolution is HistoryResolution.Year) {
            repo.resolution = HistoryResolution.Month(selectedDataPoint.value?.dateTime)
            previousSelectedPoint.value = selectedDataPoint.value
            resolution.set(repo.resolution)
            events.value = UiEvent.ShowMore(HistoryResolution.Month(selectedDataPoint.value?.dateTime))

        } else if (repo.resolution is HistoryResolution.Month) {
            repo.resolution = HistoryResolution.Year
            resolution.set(repo.resolution)
            events.value = UiEvent.ShowMore(HistoryResolution.Year)

        }


    }

    /**
     * Formatter for the consumption axis.
     */
    fun createConsumptionAxisFormatter(consumption: List<Consumption>) =
            object : IAxisValueFormatter {
                private val yearFormat = DateTimeFormat.forPattern("d")
                        .withLocale(Locale("sv", "SE"))
                private val monthFormat = DateTimeFormat.forPattern("HH:mm")
                        .withLocale(Locale("sv", "SE"))

                /**
                 * Create formatter based on the resolution of the repo.
                 */
                override fun getFormattedValue(value: Float, axis: AxisBase?): String =
                        consumption
                                .getOrNull(value.toInt())
                                ?.dateTime
                                ?.let {
                                    if (repo.resolution is HistoryResolution.Year) {
                                        yearFormat.print(it)
                                    } else if (repo.resolution is HistoryResolution.Month) {
                                        monthFormat.print(it)
                                    } else {
                                        ""
                                    }
                                } ?: ""
            }

    /**
     * Formatter for the temperature axis.
     */
    fun createTemperatureAxisFormatter(temperature: List<Temperature>) =
            object : IAxisValueFormatter {
                private val yearFormat = DateTimeFormat.forPattern("d").withLocale(Locale("sv", "SE"))
                private val monthFormat = DateTimeFormat.forPattern("HH:mm").withLocale(Locale("sv", "SE"))
                /**
                 * Create formatter based on the resolution of the repo.
                 */
                override fun getFormattedValue(value: Float, axis: AxisBase?): String =
                        temperature
                                .getOrNull(value.toInt())
                                ?.dateTime
                                ?.let {
                                    if (repo.resolution is HistoryResolution.Year) {
                                        yearFormat.print(it)
                                    } else if (repo.resolution is HistoryResolution.Month) {
                                        monthFormat.print(it)
                                    } else {
                                        ""
                                    }
                                } ?: ""
            }


    /**
     * Formatter for the temperature axis.
     */
    fun createPriceAxisFormatter(price: List<Price>) =
            object : IAxisValueFormatter {
                private val yearFormat = DateTimeFormat.forPattern("d").withLocale(Locale("sv", "SE"))
                private val monthFormat = DateTimeFormat.forPattern("HH:mm").withLocale(Locale("sv", "SE"))
                /**
                 * Create formatter based on the resolution of the repo.
                 */
                override fun getFormattedValue(value: Float, axis: AxisBase?): String =
                        price
                                .getOrNull(value.toInt())
                                ?.dateTime
                                ?.let {
                                    if (repo.resolution is HistoryResolution.Year) {
                                        yearFormat.print(it)
                                    } else if (repo.resolution is HistoryResolution.Month) {
                                        monthFormat.print(it)
                                    } else {
                                        ""
                                    }
                                } ?: ""
            }

    /**
     * Get the axis formatter from the navigation view.
     */
    fun createNavigationAxisFormatter(navigationData: List<NavigationDataPoint>) =
            object : IAxisValueFormatter {
                private val yearFormat = DateTimeFormat.forPattern("MMM").withLocale(Locale("sv", "SE"))
                private val monthFormat = DateTimeFormat.forPattern("dd EEE").withLocale(Locale("sv", "SE"))
                /**
                 * Get the formatted value for [value].
                 */
                override fun getFormattedValue(value: Float, axis: AxisBase?): String {
                    var data = navigationData.getOrNull(value.toInt())


                    return data?.let { data ->


                        if (!data.selectable) return ""

                        data.usage?.let {
                            if (it <= 0f) return ""
                        }

                        return data?.dateTime
                                ?.let {
                                    when (repo.resolution) {
                                        is HistoryResolution.Year -> yearFormat.print(it).formatDate().toUpperCase()
                                        is HistoryResolution.Month -> monthFormat.print(it).replace(" ", "\n").formatDate().toUpperCase()
                                    }
                                } ?: ""

                    } ?: ""


                }


            }

    /**
     * Create an axis formatter for the min max view.
     */
    fun createMinMaxAxisFormatter(minMax: List<MinMaxDataPoint>) =
            object : IAxisValueFormatter {
                private val yearFormat = DateTimeFormat.forPattern("dd EEE").withLocale(Locale("sv", "SE"))
                private val monthFormat = DateTimeFormat.forPattern("HH:mm").withLocale(Locale("sv", "SE"))

                /**
                 * Get the formatted value based on the value.
                 */
                override fun getFormattedValue(value: Float, axis: AxisBase?): String =
                        minMax.getOrNull(value.toInt())
                                ?.dateTime
                                ?.let {
                                    if (repo.resolution is HistoryResolution.Year && it.dayOfWeek == DateTimeConstants.SUNDAY) {
                                        yearFormat.print(it)
                                    } else if (repo.resolution is HistoryResolution.Month && value.toInt() % 4 == 0) {
                                        monthFormat.print(it)
                                    } else {
                                        ""
                                    }
                                } ?: ""
            }

    /**
     * Get consumption for a specified [date].
     */
    fun getConsumption(date: DateTime): LiveData<UsageResponse> {
        val liveData = consumption[date] ?: MutableLiveData()

        if (liveData.value == null) fetchConsumption(date, liveData)
        if (consumption[date] == null) consumption[date] = liveData

        return liveData
    }

    /**
     * Get the temperature data for a specified [date].
     */
    fun getTemperature(date: DateTime): LiveData<TemperatureResponse> {
        val liveData = temperature[date] ?: MutableLiveData()

        if (liveData.value == null) fetchTemperature(date, liveData)
        if (temperature[date] == null) temperature[date] = liveData

        return liveData
    }

    /**
     * Get the price data for a specified [date].
     */
    fun getPrice(date: DateTime): LiveData<PriceResponse> {
        val liveData = price[date] ?: MutableLiveData()

        if (liveData.value == null) fetchPrice(date, liveData)
        if (price[date] == null) price[date] = liveData

        return liveData
    }

    /**
     * Get the distribution data for a specified [date].
     */
    fun getDistribution(date: DateTime): LiveData<List<DistributionDataPoint>> {
        val liveData = distribution[date] ?: MutableLiveData()

        if (liveData.value == null) fetchDistribution(date, liveData)
        if (distribution[date] == null) distribution[date] = liveData

        return liveData
    }

    /**
     * Get min max data for a specified [date].
     */
    fun getMinMax(date: DateTime): LiveData<MinMaxResponse> {
        val liveData = minMax[date] ?: MutableLiveData()

        if (liveData.value == null) fetchMinMax(date, liveData)
        if (minMax[date] == null) minMax[date] = liveData

        return liveData
    }


    /**
     * When the user wants to sign the poa.
     */
    fun signPoa() {
        events.value = UiEvent.StartActivity(SignatureActivity::class.java, MainActivity.SIGN_POA_REQUEST)
    }

    /**
     * When getting activity results.
     */
    @SuppressLint("VisibleForTests")
    fun onActivityResult(requestCode: Int, responseCode: Int, intent: Intent?) {
        initializeRepo(HistoryResolution.Year)
        when (requestCode) {
            MainActivity.SIGN_POA_REQUEST -> {
                if (responseCode == Activity.RESULT_OK) {
                    fetchHistory()
                }
            }
        }
    }

    /**
     * The currently selected data point.
     */
    fun getSelectedDataPoint(): LiveData<NavigationDataPoint> = selectedDataPoint

    /**
     * If the view is currently loading.
     */
    fun isLoading(): LiveData<Boolean> = loading

    /**
     * If min max is currently loading.
     */
    fun isMinMaxLoading(): LiveData<Boolean> = minMaxLoading

    /**
     * If distribution data is loading or not.
     */
    fun isDistributionLoading(): LiveData<Boolean> = distributionLoading

    /**
     * The current route index of the navigation.
     */
    fun getNavigationPageIndex(): LiveData<Int> = navigationPageIndex

    /**
     * The components that should be displayed in the view.
     */
    fun getComponents(): LiveData<List<HistoryComponent>> = components

    /**
     * If the consumption is currently loading.
     */
    fun isConsumptionLoading(): LiveData<Boolean> = consumptionLoading

    /**
     * If the temperature is currently loading.
     */
    fun isTemperatureLoading(): LiveData<Boolean> = temperatureLoading

    /**
     * If the price is currently loading.
     */
    fun isPriceLoading(): LiveData<Boolean> = priceLoading

    /**
     * Stream of events.
     */
    fun getEvents(): LiveData<UiEvent> = events

    private fun fetchMinMax(date: DateTime, liveData: MutableLiveData<MinMaxResponse>) {
        repo.getMinMax(date)
                .doOnSubscribe { minMaxLoading.value = true }
                .doOnTerminate { minMaxLoading.value = false }
                .doOnDispose { minMaxLoading.value = false }
                .subscribeBy(
                        onNext = {
                            liveData.value = it
                        },
                        onError = {
                            minMax.remove(date)
                            events.value = UiEvent.ShowError(it)
                        }
                )
    }

    private fun fetchConsumption(date: DateTime, liveData: MutableLiveData<UsageResponse>) {
        repo.getUsage(date)
                .doOnSubscribe { consumptionLoading.value = true }
                .doOnTerminate { consumptionLoading.value = false }
                .doOnDispose { consumptionLoading.value = false }
                .subscribeBy(
                        onNext = {
                            liveData.value = it
                        },
                        onError = {
                            consumption.remove(date)
                            events.value = UiEvent.ShowError(it)
                        }
                )
    }

    private fun fetchDistribution(date: DateTime, liveData: MutableLiveData<List<DistributionDataPoint>>) {
        repo.getDistribution(date)
                .doOnSubscribe { distributionLoading.value = true }
                .doOnTerminate { distributionLoading.value = false }
                .doOnDispose { distributionLoading.value = false }
                .subscribeBy(
                        onNext = {
                            liveData.value = it
                        },
                        onError = {
                            distribution.remove(date)
                            events.value = UiEvent.ShowError(it)
                        }
                )
    }

    private fun fetchTemperature(date: DateTime, liveData: MutableLiveData<TemperatureResponse>) {
        repo.getTemperature(date)
                .doOnSubscribe { temperatureLoading.value = true }
                .doOnTerminate { temperatureLoading.value = false }
                .doOnDispose { temperatureLoading.value = false }
                .subscribeBy(
                        onNext = {
                            liveData.value = it
                        },
                        onError = {
                            temperature.remove(date)
                            events.value = UiEvent.ShowError(it)
                        }
                )
    }

    private fun fetchPrice(date: DateTime, liveData: MutableLiveData<PriceResponse>) {
        repo.getPrice(date)
                .doOnSubscribe { priceLoading.value = true }
                .doOnTerminate { priceLoading.value = false }
                .doOnDispose { priceLoading.value = false }
                .subscribeBy(
                        onNext = {
                            liveData.value = it
                        },
                        onError = {
                            price.remove(date)
                            events.value = UiEvent.ShowError(it)
                        }
                )
    }

    fun fetchHistory() {
        repo.getHistory()
                .doOnSubscribe { loading.value = true }
                .doOnDispose { loading.value = false }
                .doOnTerminate { loading.value = false }
                .subscribeBy(
                        onNext = {

                            if(it.state == HistoryState.NEEDS_POA)isPoaMissing.set(true)else isPoaMissing.set(false)


                            when(it.state)
                            {
                                HistoryState.NEEDS_POA->{
                                    noContentTitle.set(R.string.needs_poa_title)
                                    noContentBody.set(null)
                                    isDataAvailable.set(false)
                                }
                                HistoryState.AWATING_DATA->{
                                    noContentTitle.set(R.string.needs_poa_title)
                                    noContentBody.set(R.string.needs_poa_body)
                                    isDataAvailable.set(false)
                                }
                                else ->{
                                    isDataAvailable.set(true)
                                }

                            }

                            history.value = it

                            if(isDataAvailable.get())
                            {
                                components.value = it.components
                                updateNavigationIndex(it)
                                updateSelectedDataPoint(it)
                            }

                        },
                        onError = {
                            it.printStackTrace()
                            if(repo.resolution is HistoryResolution.Year)
                            events.value = UiEvent.ShowError(it)
                        }
                )
    }

    private fun createDescriptionLabel(date: DateTime) =
            if (repo.resolution == HistoryResolution.Year) {
                DateTimeFormat
                        .forPattern("MMMM YYYY")
                        .withLocale(Locale("sv", "SE"))
                        .print(date)
            } else {
                DateTimeFormat
                        .forPattern("dd MMMM YYYY")
                        .withLocale(Locale("sv", "SE"))
                        .print(date)
            }

    private fun updateSelectedDataPoint(historyResponse: HistoryResponse) {

        if (!(repo.resolution is HistoryResolution.Month)) {
            if (previousSelectedPoint.value != null) {
                selectDataPoint(previousSelectedPoint.value!!)
                return
            }


            if (historyResponse.navigationData.flatMap { it.data }.contains(selectedDataPoint.value)) {
                selectedDataPoint.value?.let {
                    selectDataPoint(it)
                }
            } else {
                getAutoSelectPoint(historyResponse.navigationData)?.let {
                    selectDataPoint(it)
                }
            }
        } else
            getAutoSelectPoint(historyResponse.navigationData)?.let {
                selectDataPoint(it)
            }

    }


    private fun getAutoSelectPoint(navigationData: List<NavigationData>) =
            navigationData
                    .last()
                    .data
                    .lastOrNull { it.selectable && it.hasDetailedData }
                    ?: (navigationData.last().data.lastOrNull { it.selectable }
                            ?: navigationData.lastOrNull()?.data?.lastOrNull())


    private fun updateNavigationIndex(historyResponse: HistoryResponse) {
        val needsIndexUpdate = navigationPageIndex.value?.let { index ->
            !(index >= 0 && index < historyResponse.navigationData.size)
        } ?: true

        if (needsIndexUpdate) {
            navigationPageIndex.value = historyResponse.navigationData.size - 1
        } else {
            // Forced update.
            navigationPageIndex.value = navigationPageIndex.value
        }
    }


    fun setSelectionFromFeed(sum: Int?, timeStamp: Long) {
        selectedChart.set( R.id.priceChart)
        var dataPointFromFeed = NavigationDataPoint(timeStamp / 1000, sum, true, true, timeStamp / 1000)
        history.value?.let {

            if (repo.resolution is HistoryResolution.Year) {
                if (it.navigationData.flatMap { it.data }.contains(dataPointFromFeed)) {
                    selectedDataPoint.value = dataPointFromFeed
                } else {
                    getAutoSelectPoint(it.navigationData)?.let {
                        selectDataPoint(it)
                    }
                }
            }
            else
                setDataPointAndFetchHistory(dataPointFromFeed)

        } ?: setDataPointAndFetchHistory(dataPointFromFeed)

    }

    private fun setDataPointAndFetchHistory(navigationDataPoint: NavigationDataPoint) {
        this.previousSelectedPoint.value=null
        history.value = null
        this.resolution.set(HistoryResolution.Year)
        getHistory(HistoryResolution.Year)
        selectDataPoint(navigationDataPoint)
    }


    fun getDataForChart(id: Int, date: DateTime) {
        selectedChart.set(id)
        when (id) {
            R.id.totalChart -> {
                getConsumption(date)
            }
            R.id.temperatureChart -> {
                getTemperature(date)
            }
            R.id.priceChart ->  {
                getPrice(date)
            }
        }

    }

    fun navigateToRetail()
    {
        events.value=UiEvent.ShowRetail(resolution.get())
    }

    private fun setSelectedYear()
    {
        if (resolution.get() == null)
            selectedResolution.set(R.id.years)
        else if (resolution.get() == HistoryResolution.Year)
            selectedResolution.set(R.id.years)
        else
            selectedResolution.set(R.id.months)

    }

}

