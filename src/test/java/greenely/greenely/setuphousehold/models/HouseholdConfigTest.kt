package greenely.greenely.setuphousehold.models

import org.junit.Assert.*
import org.junit.Test


class HouseholdConfigTest{

    @Test
    fun testModel(){

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


        val model = HouseholdConfig(
                introText = "Intro",
                introTitle = "Intro",
                municipalities = municipalityOptions,
                facilityTypes = facilityTypesOptions,
                facilityAreas = facilityAreaOptions,
                heatingTypes = heatingTypesOptions,
                occupants = occupants,
                electricCarCounts = electricCarCount,
                constructionYears = constructionYearOptions
        )

        assertEquals(municipalityOptions, model.municipalities)
        assertEquals(facilityTypesOptions, model.facilityTypes)
        assertEquals(facilityAreaOptions, model.facilityAreas)
        assertEquals(heatingTypesOptions, model.heatingTypes)
        assertEquals(constructionYearOptions, model.constructionYears)
        assertEquals(occupants, model.occupants)
        assertEquals(electricCarCount, model.electricCarCounts)


    }
}