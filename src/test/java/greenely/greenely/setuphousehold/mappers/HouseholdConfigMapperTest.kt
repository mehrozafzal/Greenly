package greenely.greenely.setuphousehold.mappers

import greenely.greenely.setuphousehold.models.HouseholdConfig
import greenely.greenely.setuphousehold.models.HouseholdInputOptions
import greenely.greenely.setuphousehold.models.json.HouseholdConfigJsonModel
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

class HouseholdConfigMapperTest{


    private lateinit var mapper: HouseholdConfigMapper

    @Before
    fun setUp() {

        mapper = HouseholdConfigMapper()
    }

    @Test
    fun testFromHouseholdConfigJson(){

        //Given
        val municipalityOptions = listOf(
                HouseholdInputOptions(1440, "Ale"),
                HouseholdInputOptions(1489, "Alingsås"),
                HouseholdInputOptions(764, "Alvesta")
        )

        val facilityTypesOptions = listOf(
                HouseholdInputOptions(1, "facility 1"),
                HouseholdInputOptions(2, "facility 2"),
                HouseholdInputOptions(3, "facility 3"),
                HouseholdInputOptions(4,"facility 4")
        )

        val heatingTypesOptions = listOf(
                HouseholdInputOptions(1, "Heating 1"),
                HouseholdInputOptions(2, "Heating 2"),
                HouseholdInputOptions(3, "Heating 3")
        )

        val facilityAreaOptions = listOf(
                HouseholdInputOptions(1, "area 1"),
                HouseholdInputOptions(2, "area 2"),
                HouseholdInputOptions(3, "area 3")
        )

        val constructionYearOptions = listOf(
                HouseholdInputOptions(1, "1950-1960"),
                HouseholdInputOptions(2, "1960-1970"),
                HouseholdInputOptions(3, "1970-1980")
        )

        val occupants = listOf(
                HouseholdInputOptions(1, "1950-1960"),
                HouseholdInputOptions(2, "1960-1970"),
                HouseholdInputOptions(3, "1970-1980")
        )

        val electricCarCount = listOf(
                HouseholdInputOptions(1, "1950-1960"),
                HouseholdInputOptions(2, "1960-1970"),
                HouseholdInputOptions(3, "1970-1980")
        )


        val expectedModel = HouseholdConfig(
                introText = "Intro Text",
                introTitle = "Intro Title",
                municipalities = municipalityOptions,
                facilityTypes = facilityTypesOptions,
                facilityAreas = facilityAreaOptions,
                heatingTypes = heatingTypesOptions,
                occupants = occupants,
                electricCarCounts = electricCarCount,
                constructionYears = constructionYearOptions
        )

        val json = HouseholdConfigJsonModel(
                "Intro Title",
                "Intro Text",
                municipalityOptions,
                facilityTypesOptions,
                heatingTypesOptions,
                constructionYearOptions,
                occupants,
                facilityAreaOptions,
                electricCarCount
        )

        //when
        val householdConfig = mapper.fromHouseholdConfigJson(json)

        //then

        assertThat(householdConfig).isEqualTo(expectedModel)

    }
}
