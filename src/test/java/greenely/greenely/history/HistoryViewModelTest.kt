@file:Suppress("KDocMissingDocumentation")

package greenely.greenely.history

import android.app.Activity
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import android.content.Intent
import greenely.greenely.main.ui.MainActivity
import greenely.greenely.R
import greenely.greenely.DefaultTimezone
import greenely.greenely.anyObject
import greenely.greenely.signature.ui.SignatureActivity
import io.reactivex.Observable
import org.assertj.core.api.Assertions.assertThat
import org.joda.time.DateTime
import org.joda.time.DateTimeConstants
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class HistoryViewModelTest {

    @Rule
    @JvmField
    var defaultTimezone = DefaultTimezone()

    @Mock
    lateinit var repoFactory: HistoryRepoFactory

    @Mock
    lateinit var repo: HistoryRepo

    @InjectMocks
    lateinit var viewModel: HistoryViewModel

    @Rule
    @JvmField
    val executorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        `when`(repoFactory.create(anyObject())).thenReturn(repo)
    }

    @Test
    fun testHistory() {
        val dataPoint = NavigationDataPoint(
                DateTime(2017, 12, 1, 0, 0).millis / 1000,
                1000,
                true,
                true,
                null
        )
        val historyResponse = HistoryResponse(
                HistoryState.HAS_DATA,
                listOf(
                        NavigationData(
                                2016,
                                1000,
                                listOf(
                                        NavigationDataPoint(
                                                DateTime.now().millis / 1000,
                                                1000,
                                                true,
                                                true,
                                                null
                                        )
                                )
                        ),
                        NavigationData(
                                2017,
                                1000,
                                listOf(
                                        dataPoint
                                )
                        )
                ),
                listOf(
                        HistoryComponent.USAGE
                )
        )

        `when`(repo.getHistory()).thenReturn(Observable.just(historyResponse))

        val historyLoading = mutableListOf<Boolean>()
        viewModel.isLoading().observeForever {
            it?.let { historyLoading += it }
        }

        var history: HistoryResponse? = null
        viewModel.getHistory(HistoryResolution.Year).observeForever {
            it?.let { history = it }
        }

        assertThat(history).isEqualTo(historyResponse)
        assertThat(viewModel.getSelectedDataPoint().value).isEqualTo(dataPoint)
        assertThat(historyLoading).isEqualTo(listOf(true, false))
        assertThat(viewModel.getNavigationPageIndex().value).isEqualTo(1)
        assertThat(viewModel.getComponents().value).isEqualTo(listOf(HistoryComponent.USAGE))
    }

    @Test
    fun testHistory_savedState() {
        val dataPoint = NavigationDataPoint(
                DateTime(2017, 12, 1, 0, 0).millis / 1000,
                1000,
                true,
                true,
                null)
        val historyResponse = HistoryResponse(
                HistoryState.HAS_DATA,
                listOf(
                        NavigationData(
                                2016,
                                1000,
                                listOf(
                                        NavigationDataPoint(
                                                DateTime.now().millis / 1000,
                                                1000,
                                                true,
                                                true,
                                                null)
                                )
                        ),
                        NavigationData(
                                2017,
                                1000,
                                listOf(
                                        dataPoint
                                )
                        )
                ),
                listOf(
                        HistoryComponent.USAGE
                )
        )

        `when`(repo.getHistory()).thenReturn(Observable.just(historyResponse))

        viewModel.getHistory(HistoryResolution.Year)

        viewModel.updatePageIndex(0)
        viewModel.selectDataPoint(historyResponse.navigationData[0].data[0])

        viewModel.getHistory(HistoryResolution.Year)

        assertThat(viewModel.getNavigationPageIndex().value).isEqualTo(0)
        assertThat(viewModel.getSelectedDataPoint().value)
                .isEqualTo(historyResponse.navigationData[0].data[0])
    }

    @Test
    fun testHistory_needsPoa() {
        val historyResponse = HistoryResponse(
                HistoryState.NEEDS_POA,
                listOf(),
                listOf()
        )
        `when`(repo.getHistory()).thenReturn(Observable.just(historyResponse))


        var history: HistoryResponse? = null
        viewModel.getHistory(HistoryResolution.Year).observeForever {
            it?.let { history = it }
        }

        assertThat(history).isEqualTo(historyResponse)
        assertThat(viewModel.isPoaMissing.get()).isTrue()
        assertThat(viewModel.noContentTitle.get()).isEqualTo(R.string.needs_poa_title)
        assertThat(viewModel.noContentBody.get()).isEqualTo(R.string.needs_poa_body)
    }

    @Test
    fun testHistory_cached() {
        val dataPoint = NavigationDataPoint(
                DateTime(2017, 12, 1, 0, 0).millis / 1000,
                1000,
                true,
                true,
                null        )
        val historyResponse = HistoryResponse(
                HistoryState.HAS_DATA,
                listOf(
                        NavigationData(
                                2016,
                                1000,
                                listOf(
                                        NavigationDataPoint(
                                                DateTime.now().millis / 1000,
                                                1000,
                                                true,
                                                true,
                                                null)
                                )
                        ),
                        NavigationData(
                                2017,
                                1000,
                                listOf(
                                        dataPoint
                                )
                        )
                ),
                listOf(
                        HistoryComponent.USAGE
                )
        )

        `when`(repo.getHistory()).thenReturn(Observable.just(historyResponse))

        viewModel.getHistory(HistoryResolution.Year)
        viewModel.getHistory(HistoryResolution.Year)

        verify(repo, times(1)).getHistory()
    }

    @Test
    fun testHistory_error() {
        val error = Error()
        `when`(repo.getHistory()).thenReturn(Observable.error(error))

        val historyLoading = mutableListOf<Boolean>()
        viewModel.isLoading().observeForever {
            it?.let { historyLoading += it }
        }

        val events = mutableListOf<UiEvent>()
        viewModel.getEvents().observeForever {
            it?.let { events += it }
        }

        var history: HistoryResponse? = null
        viewModel.getHistory(HistoryResolution.Year).observeForever {
            it?.let { history = it }
        }

        assertThat(history).isNull()
        assertThat(historyLoading).isEqualTo(listOf(true, false))
        assertThat(events.last()).isEqualTo(UiEvent.ShowError(error))
    }

    @Test
    fun testHistory_valueFormatterYear() {
        val dataPoint = NavigationDataPoint(
                DateTime(2017, 12, 1, 0, 0).millis / 1000, 1000, true, true,null
        )

        `when`(repo.getHistory()).thenReturn(Observable.empty())
        `when`(repo.resolution).thenReturn(HistoryResolution.Year)

        viewModel.getHistory(HistoryResolution.Year)

        assertThat(
                viewModel
                        .createNavigationAxisFormatter(listOf(dataPoint))
                        .getFormattedValue(0.0f, null)
        ).isEqualTo("dec")
    }

    @Test
    fun testHistory_valueFormatterWeek() {
        val dataPoint = NavigationDataPoint(
                DateTime(2017, 12, 1, 0, 0).millis / 1000, 1000, true, true,null
        )

        `when`(repo.getHistory()).thenReturn(Observable.empty())
        `when`(repo.resolution).thenReturn(HistoryResolution.Month(DateTime.now()))

        viewModel.getHistory(HistoryResolution.Month(DateTime.now()))

        assertThat(
                viewModel
                        .createNavigationAxisFormatter(listOf(dataPoint))
                        .getFormattedValue(0.0f, null)
        ).isEqualTo("01 fr")
    }

    @Test
    fun testSelectDataPoint_noData() {
        val date = DateTime(2017, 12, 1, 0, 0)
        val dataPoint = NavigationDataPoint(date.millis / 1000, 1000, true, false,null)

        `when`(repo.resolution).thenReturn(HistoryResolution.Year)

        viewModel.initializeRepo(HistoryResolution.Year)
        viewModel.selectDataPoint(dataPoint)

        assertThat(viewModel.noContentTitle.get()).isEqualTo(R.string.no_content_title)
        assertThat(viewModel.noContentBody.get()).isEqualTo(R.string.no_content_body)
    }

    @Test
    fun testSelectDataPoint_noDataNoPoa() {
        val date = DateTime(2017, 12, 1, 0, 0)
        val dataPoint = NavigationDataPoint(date.millis / 1000, 1000, true, false,null)

        `when`(repo.resolution).thenReturn(HistoryResolution.Year)

        viewModel.initializeRepo(HistoryResolution.Year)
        viewModel.isPoaMissing.set(true)
        viewModel.noContentTitle.set(R.string.needs_poa_title)
        viewModel.noContentBody.set(R.string.needs_poa_body)
        viewModel.selectDataPoint(dataPoint)

        assertThat(viewModel.noContentTitle.get()).isEqualTo(R.string.needs_poa_title)
        assertThat(viewModel.noContentBody.get()).isEqualTo(R.string.needs_poa_body)
    }

    @Test
    fun testInitializeRepo() {
        viewModel.initializeRepo(HistoryResolution.Year)
        verify(repoFactory, times(1)).create(HistoryResolution.Year)
    }

    @Test
    fun testConsumption() {
        val usageResponse = UsageResponse(
                listOf(
                        Consumption(
                                DateTime.now().withDayOfWeek(DateTimeConstants.SUNDAY).millis / 1000,
                                1000
                        )
                ),
                1000
        )
        val date = DateTime.now()

        `when`(repo.getHistory()).thenReturn(Observable.empty())
        `when`(repo.getUsage(date)).thenReturn(
                Observable.just(usageResponse)
        )


        viewModel.getHistory(HistoryResolution.Year)
        val consumptionLoading = mutableListOf<Boolean>()
        viewModel.isConsumptionLoading().observeForever {
            it?.let { consumptionLoading += it }
        }

        var usageData: UsageResponse? = null
        viewModel.getConsumption(date).observeForever {
            it?.let { usageData = it }
        }

        assertThat(consumptionLoading).isEqualTo(listOf(true, false))
        assertThat(usageData).isEqualTo(usageResponse)
    }

    @Test
    fun testConsumptionCached() {
        val usageResponse = UsageResponse(
                listOf(
                        Consumption(
                                DateTime.now().withDayOfWeek(DateTimeConstants.SUNDAY).millis / 1000,
                                1000
                        )
                ),
                1000
        )
        val date = DateTime.now()

        `when`(repo.getHistory()).thenReturn(Observable.empty())
        `when`(repo.getUsage(date)).thenReturn(
                Observable.just(usageResponse)
        )

        viewModel.getHistory(HistoryResolution.Year)

        viewModel.getConsumption(date)
        viewModel.getConsumption(date)

        verify(repo, times(1)).getUsage(date)
    }

    @Test
    fun testConsumption_error() {
        val date = DateTime.now()
        val error = Error()
        `when`(repo.getUsage(date)).thenReturn(Observable.error(error))

        viewModel.initializeRepo(HistoryResolution.Year)

        val events = mutableListOf<UiEvent>()
        viewModel.getEvents().observeForever {
            it?.let { events += it }
        }

        var usageData: UsageResponse? = null
        viewModel.getConsumption(date).observeForever {
            it?.let { usageData = it }
        }

        assertThat(usageData).isNull()
        assertThat(events.last()).isEqualTo(UiEvent.ShowError(error))

        viewModel.getConsumption(date)

        verify(repo, times(2)).getUsage(date)
    }

    @Test
    fun testConsumption_valueFormatterYear() {
        val consumption = Consumption(
                DateTime(2017, 12, 1, 0, 0).withDayOfWeek(DateTimeConstants.SUNDAY).millis / 1000,
                1000
        )

        `when`(repo.resolution).thenReturn(HistoryResolution.Year)
        `when`(repo.getHistory()).thenReturn(Observable.empty())

        viewModel.getHistory(HistoryResolution.Year)
        assertThat(
                viewModel
                        .createConsumptionAxisFormatter(listOf(consumption))
                        .getFormattedValue(0.0f, null)
        ).isEqualTo("03 sö")
    }

    @Test
    fun testConsumption_valueFormatterWeek() {
        val consumption = Consumption(
                DateTime(2017, 12, 1, 0, 0).withDayOfWeek(DateTimeConstants.SUNDAY).millis / 1000,
                1000
        )

        `when`(repo.resolution).thenReturn(HistoryResolution.Month(DateTime.now()))
        `when`(repo.getHistory()).thenReturn(Observable.empty())

        viewModel.getHistory(HistoryResolution.Month(DateTime.now()))
        assertThat(
                viewModel
                        .createConsumptionAxisFormatter(listOf(consumption))
                        .getFormattedValue(0.0f, null)
        ).isEqualTo("00:00")
    }

    @Test
    fun testTemperature() {
        val date = DateTime(2017, 12, 1, 0, 0)
        val temperatureResponse = TemperatureResponse(
                listOf(Temperature(date.millis / 1000, 1000, 100)),
                1000,
                100,
                100
        )

        `when`(repo.getTemperature(date)).thenReturn(Observable.just(temperatureResponse))

        viewModel.initializeRepo(HistoryResolution.Year)

        val temperatureLoading = mutableListOf<Boolean>()
        viewModel.isTemperatureLoading().observeForever {
            it?.let { temperatureLoading += it }
        }

        var temperature: TemperatureResponse? = null
        viewModel.getTemperature(date).observeForever {
            it?.let { temperature = it }
        }

        assertThat(temperature).isEqualTo(temperatureResponse)
        assertThat(temperatureLoading).isEqualTo(listOf(true, false))
    }

    @Test
    fun testTemperature_cached() {
        val date = DateTime(2017, 12, 1, 0, 0)
        val temperatureResponse = TemperatureResponse(
                listOf(Temperature(date.millis / 1000, 1000, 100)),
                1000,
                100,
                100
        )

        `when`(repo.getTemperature(date)).thenReturn(Observable.just(temperatureResponse))

        viewModel.initializeRepo(HistoryResolution.Year)

        viewModel.getTemperature(date)
        viewModel.getTemperature(date)

        verify(repo, times(1)).getTemperature(date)
    }

    @Test
    fun testTemperature_error() {
        val error = Error()
        val date = DateTime(2017, 12, 1, 0, 0)

        `when`(repo.getTemperature(date)).thenReturn(Observable.error(error))

        viewModel.initializeRepo(HistoryResolution.Year)

        val temperatureLoading = mutableListOf<Boolean>()
        viewModel.isTemperatureLoading().observeForever {
            it?.let { temperatureLoading += it }
        }

        val events = mutableListOf<UiEvent>()
        viewModel.getEvents().observeForever {
            it?.let { events += it }
        }

        var temperature: TemperatureResponse? = null
        viewModel.getTemperature(date).observeForever {
            it?.let { temperature = it }
        }

        assertThat(temperature).isNull()
        assertThat(temperatureLoading).isEqualTo(listOf(true, false))
        assertThat(events.last()).isEqualTo(UiEvent.ShowError(error))

        viewModel.getTemperature(date)

        verify(repo, times(2)).getTemperature(date)
    }

    @Test
    fun testTemperature_valueFormatterYear() {
        val temperature = Temperature(
                DateTime(2017, 12, 1, 0, 0).withDayOfWeek(DateTimeConstants.SUNDAY).millis / 1000,
                1000,
                100
        )

        `when`(repo.resolution).thenReturn(HistoryResolution.Year)
        `when`(repo.getHistory()).thenReturn(Observable.empty())

        viewModel.getHistory(HistoryResolution.Year)
        assertThat(
                viewModel
                        .createTemperatureAxisFormatter(listOf(temperature))
                        .getFormattedValue(0.0f, null)
        ).isEqualTo("03 sö")
    }

    @Test
    fun testTemperature_valueFormatterWeek() {
        val temperature = Temperature(
                DateTime(2017, 12, 1, 0, 0).withDayOfWeek(DateTimeConstants.SUNDAY).millis / 1000,
                1000,
                100
        )

        `when`(repo.resolution).thenReturn(HistoryResolution.Month(DateTime.now()))
        `when`(repo.getHistory()).thenReturn(Observable.empty())

        viewModel.getHistory(HistoryResolution.Month(DateTime.now()))
        assertThat(
                viewModel
                        .createTemperatureAxisFormatter(listOf(temperature))
                        .getFormattedValue(0.0f, null)
        ).isEqualTo("00:00")
    }

    @Test
    fun testDistribution() {
        val date = DateTime(2017, 12, 1, 1, 0, 0)
        val distributionPoints = listOf(DistributionDataPoint("start", "end", 10000))

        `when`(repo.getDistribution(date)).thenReturn(Observable.just(distributionPoints))

        viewModel.initializeRepo(HistoryResolution.Year)

        val distributionLoading = mutableListOf<Boolean>()
        viewModel.isDistributionLoading().observeForever {
            it?.let { distributionLoading += it }
        }

        var distributions: List<DistributionDataPoint>? = null
        viewModel.getDistribution(date).observeForever {
            it?.let { distributions = it }
        }

        assertThat(distributions).isEqualTo(distributionPoints)
        assertThat(distributionLoading).isEqualTo(listOf(true, false))
    }

    @Test
    fun testDistribution_cached() {
        val date = DateTime(2017, 12, 1, 1, 0, 0)
        val distributionPoints = listOf(DistributionDataPoint("start", "end", 10000))

        `when`(repo.getDistribution(date)).thenReturn(Observable.just(distributionPoints))

        viewModel.initializeRepo(HistoryResolution.Year)

        viewModel.getDistribution(date)
        viewModel.getDistribution(date)

        verify(repo, times(1)).getDistribution(date)
    }

    @Test
    fun testDistribution_error() {
        val date = DateTime(2017, 12, 1, 1, 0, 0)
        val error = Error()

        `when`(repo.getDistribution(date)).thenReturn(Observable.error(error))

        viewModel.initializeRepo(HistoryResolution.Year)

        val distributionLoading = mutableListOf<Boolean>()
        viewModel.isDistributionLoading().observeForever {
            it?.let { distributionLoading += it }
        }

        val events = mutableListOf<UiEvent>()
        viewModel.getEvents().observeForever { it?.let { events += it } }

        var distributions: List<DistributionDataPoint>? = null
        viewModel.getDistribution(date).observeForever {
            it?.let { distributions = it }
        }

        assertThat(distributions).isNull()
        assertThat(distributionLoading).isEqualTo(listOf(true, false))
        assertThat(events.last()).isEqualTo(UiEvent.ShowError(error))

        viewModel.getDistribution(date)

        verify(repo, times(2)).getDistribution(date)
    }

    @Test
    fun testMinMax() {
        val date = DateTime(2017, 12, 1, 0, 0)
        val minMaxResponse = MinMaxResponse(
                listOf(
                        MinMaxDataPoint(date.millis / 1000, 1000, 1000, 1000)
                ),
                1000
        )

        `when`(repo.getMinMax(date)).thenReturn(Observable.just(minMaxResponse))

        viewModel.initializeRepo(HistoryResolution.Year)

        val minMaxLoading = mutableListOf<Boolean>()
        viewModel.isMinMaxLoading().observeForever {
            it?.let { minMaxLoading += it }
        }

        var minMax: MinMaxResponse? = null
        viewModel.getMinMax(date).observeForever {
            it?.let { minMax = it }
        }

        assertThat(minMax).isEqualTo(minMaxResponse)
        assertThat(minMaxLoading).isEqualTo(listOf(true, false))
    }

    @Test
    fun testMinMax_cached() {
        val date = DateTime(2017, 12, 1, 0, 0)
        val minMaxResponse = MinMaxResponse(
                listOf(
                        MinMaxDataPoint(date.millis / 1000, 1000, 1000, 1000)
                ),
                1000
        )

        `when`(repo.getMinMax(date)).thenReturn(Observable.just(minMaxResponse))

        viewModel.initializeRepo(HistoryResolution.Year)
        viewModel.getMinMax(date)
        viewModel.getMinMax(date)

        verify(repo, times(1)).getMinMax(date)
    }

    @Test
    fun testMinMax_error() {
        val date = DateTime(2017, 12, 1, 0, 0)
        val error = Error()

        `when`(repo.getMinMax(date)).thenReturn(Observable.error(error))

        viewModel.initializeRepo(HistoryResolution.Year)

        val minMaxLoading = mutableListOf<Boolean>()
        viewModel.isMinMaxLoading().observeForever {
            it?.let { minMaxLoading += it }
        }

        val events = mutableListOf<UiEvent>()
        viewModel.getEvents().observeForever {
            it?.let { events += it }
        }

        var minMax: MinMaxResponse? = null
        viewModel.getMinMax(date).observeForever {
            it?.let { minMax = it }
        }

        assertThat(minMax).isNull()
        assertThat(minMaxLoading).isEqualTo(listOf(true, false))
        assertThat(events.last()).isEqualTo(UiEvent.ShowError(error))

        viewModel.getMinMax(date)

        verify(repo, times(2)).getMinMax(date)
    }

    @Test
    fun testMinMax_valueFormatterYear() {
        val minMax = MinMaxDataPoint(
                DateTime(2017, 12, 1, 0, 0).withDayOfWeek(DateTimeConstants.SUNDAY).millis / 1000,
                1000,
                1000,
                1000
        )

        `when`(repo.resolution).thenReturn(HistoryResolution.Year)
        `when`(repo.getHistory()).thenReturn(Observable.empty())

        viewModel.getHistory(HistoryResolution.Year)
        assertThat(
                viewModel
                        .createMinMaxAxisFormatter(listOf(minMax))
                        .getFormattedValue(0.0f, null)
        ).isEqualTo("03 sö")
    }

    @Test
    fun testMinMax_valueFormatterWeek() {
        val minMax = MinMaxDataPoint(
                DateTime(2017, 12, 1, 0, 0).withDayOfWeek(DateTimeConstants.SUNDAY).millis / 1000,
                1000,
                1000,
                1000
        )

        `when`(repo.resolution).thenReturn(HistoryResolution.Month(DateTime.now()))
        `when`(repo.getHistory()).thenReturn(Observable.empty())

        viewModel.getHistory(HistoryResolution.Month(DateTime.now()))
        assertThat(
                viewModel
                        .createMinMaxAxisFormatter(listOf(minMax))
                        .getFormattedValue(0.0f, null)
        ).isEqualTo("00:00")
    }

    @Test
    fun testSignPoa() {
        val events = mutableListOf<UiEvent>()
        viewModel.getEvents().observeForever { it?.let { events += it } }

        viewModel.signPoa()

        assertThat(events.last())
                .isEqualTo(
                        UiEvent.StartActivity(
                                SignatureActivity::class.java,
                                MainActivity.SIGN_POA_REQUEST
                        )
                )
    }

    @Test
    fun testActivityResult() {
        val requestCode = MainActivity.SIGN_POA_REQUEST
        val responseCode = Activity.RESULT_OK
        val intent = mock(Intent::class.java)
        `when`(repo.getHistory()).thenReturn(Observable.empty())

        viewModel.initializeRepo(HistoryResolution.Year)
        viewModel.onActivityResult(requestCode, responseCode, intent)

        verify(repo, times(1)).getHistory()
    }

    @After
    fun tearDown() {
        reset(repo)
    }
}


