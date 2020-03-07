package greenely.greenely.setuphousehold.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.nhaarman.mockito_kotlin.*
import greenely.greenely.models.AuthenticationInfo
import greenely.greenely.setuphousehold.data.HouseholdRepo
import greenely.greenely.setuphousehold.mappers.HouseholdConfigMapper
import greenely.greenely.setuphousehold.mappers.HouseholdRequestMapper
import greenely.greenely.setuphousehold.models.HouseholdConfig
import greenely.greenely.setuphousehold.models.HouseholdInputOptions
import greenely.greenely.setuphousehold.models.json.HouseholdConfigJsonModel
import greenely.greenely.setuphousehold.models.json.HouseholdRequestJsonModel
import greenely.greenely.setuphousehold.ui.events.Event
import io.reactivex.Observable
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.schedulers.Schedulers
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SetupHouseholdViewModelTest {

    @JvmField
    @Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var repo: HouseholdRepo
    private lateinit var viewModel: SetupHouseholdViewModel
    private lateinit var householdConfigMapper: HouseholdConfigMapper
    private lateinit var householdRequestMapper: HouseholdRequestMapper
    private lateinit var householdConfig: HouseholdConfig
    private lateinit var householdConfigJson: HouseholdConfigJsonModel
    private lateinit var householdRequestJson: HouseholdRequestJsonModel

    @Before
    fun setup() {

        RxAndroidPlugins.setMainThreadSchedulerHandler { Schedulers.from { it.run() } }
        repo = mock()

        householdConfigJson = HouseholdConfigJsonModel(
                "Intro Title",
                "Intro Text",
                listOf(HouseholdInputOptions(1, "Stockholm")),
                listOf(HouseholdInputOptions(1, "Villa")),
                listOf(HouseholdInputOptions(1, "Ved")),
                listOf(HouseholdInputOptions(1, "1993")),
                listOf(HouseholdInputOptions(1, "1000")),
                listOf(HouseholdInputOptions(1, "6")),
                listOf(HouseholdInputOptions(1, "4"))
        )

        householdConfig = HouseholdConfig(
                "Intro Title",
                "Intro Text",
                listOf(HouseholdInputOptions(1, "Stockholm")),
                listOf(HouseholdInputOptions(1, "Villa")),
                listOf(HouseholdInputOptions(1, "Ved")),
                listOf(HouseholdInputOptions(1, "1993")),
                listOf(HouseholdInputOptions(1, "1000")),
                listOf(HouseholdInputOptions(1, "6")),
                listOf(HouseholdInputOptions(1, "4"))
        )

        householdRequestJson = HouseholdRequestJsonModel(
                1,
                1,
                1,
                1,
                1,
                1,
                2,
                3,
                4,
                1
        )

        householdConfigMapper = mock()
        householdRequestMapper = mock()
        viewModel = SetupHouseholdViewModel(repo, householdConfigMapper, householdRequestMapper)

    }

    @After
    fun tearDown() {
        RxAndroidPlugins.reset()
    }

    @Test
    fun testGetHouseholdConfigurationOptionsSuccess() {

        val mockObserver = mock<Observer<HouseholdConfig>>()

        repo.stub {
            on { getHouseholdConfig() } doReturn Observable.just(householdConfigJson)
        }

        householdConfigMapper.stub {
            on { fromHouseholdConfigJson(any()) } doReturn householdConfig
        }

        // when

        viewModel.householdConfigurationOptions.observeForever(mockObserver)

        var lastConfig: HouseholdConfig? = null
        viewModel.householdConfigurationOptions.observeForever {
            lastConfig = it
        }

        val mockResponseObserver = mock<Observer<HouseholdConfig>>()
        viewModel.householdConfigurationOptions.observeForever(mockResponseObserver)

        val loadingStates: MutableList<Event?> = mutableListOf()
        viewModel.events.observeForever {
            loadingStates += it
        }

        val inputCaptor = argumentCaptor<HouseholdConfigJsonModel>()
        verify(householdConfigMapper).fromHouseholdConfigJson(inputCaptor.capture())

        //then
        verify(mockObserver).onChanged(householdConfig)
        assertThat(lastConfig).isEqualTo(householdConfig)

    }

    @Test
    fun testGetHouseholdConfigurationOptionsError() {
        val error = Error()
        repo.stub {
            on { getHouseholdConfig() } doReturn Observable.error<HouseholdConfigJsonModel>(error)
        }

        var lastNonNullError: Throwable? = null
        viewModel.errors.observeForever {
            it?.let {
                lastNonNullError = it
            }
        }

        //when
        viewModel.getHouseholdConfigurationOptions()

        assertThat(lastNonNullError).isEqualTo(error)
    }

    @Test
    fun testSendHouseholdInfoSuccess() {

        // Given
        val response: AuthenticationInfo = mock()
        repo.stub {
            on { setHouseholdConfig(any()) } doReturn Observable.just(response)
        }

        householdRequestMapper.stub {
            on { toHouseholdRequestJson(any()) } doReturn householdRequestJson
        }



        viewModel.householdRequest.electricCarCountId.set(1)
        viewModel.householdRequest.constructionYearId.set(1)
        viewModel.householdRequest.heatingTypeId.set(1)
        viewModel.householdRequest.secondaryHeatingTypeId.set(2)
        viewModel.householdRequest.tertiaryHeatingTypeId.set(3)
        viewModel.householdRequest.quaternaryHeatingTypeId.set(4)
        viewModel.householdRequest.facilityAreaId.set(1)
        viewModel.householdRequest.facilityTypeId.set(1)
        viewModel.householdRequest.municipalityId.set(1)
        viewModel.householdRequest.occupants.set(1)

        //when
        val loadingStates: MutableList<Boolean?> = mutableListOf()
        viewModel.disruptiveLoading.observeForever {
            loadingStates += it
        }

        var lastNonNullError: Throwable? = null
        viewModel.errors.observeForever {
            it?.let {
                lastNonNullError = it
            }
        }

        viewModel.sendHouseholdInfo(false)

        assertThat(lastNonNullError).isEqualTo(null)
        assertThat(loadingStates).isEqualTo(listOf(true, false))

    }

    @Test
    fun testSendHouseholdInfoError() {
        val error = Error()
        repo.stub {
            on { setHouseholdConfig(any()) } doReturn Observable.error(
                    error
            )
        }

        householdRequestMapper.stub {
            on { toHouseholdRequestJson(any()) } doReturn householdRequestJson
        }

        var lastNonNullError: Throwable? = null
        viewModel.errors.observeForever {
            it?.let {
                lastNonNullError = it
            }
        }

        val loadingStates: MutableList<Boolean?> = mutableListOf()
        viewModel.disruptiveLoading.observeForever {
            loadingStates += it
        }

        viewModel.sendHouseholdInfo(false)

        assertThat(lastNonNullError).isEqualTo(error)
        assertThat(loadingStates).isEqualTo(listOf(true, false))
    }
}
