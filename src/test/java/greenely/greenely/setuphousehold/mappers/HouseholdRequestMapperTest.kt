package greenely.greenely.setuphousehold.mappers

import androidx.databinding.ObservableField
import greenely.greenely.setuphousehold.models.HouseholdRequest
import greenely.greenely.setuphousehold.models.json.HouseholdRequestJsonModel
import org.assertj.core.api.Assertions.*
import org.junit.Before
import org.junit.Test

class HouseholdRequestMapperTest{

    lateinit var mapper: HouseholdRequestMapper

    @Before
    fun setup(){
        mapper = HouseholdRequestMapper()
    }

    @Test
    fun testToHouseholdRequestJson(){

        //Given
        val expected = HouseholdRequestJsonModel(
                1,
                1,
                1,
                2,
                3,
                4,
                5,
                1,
                1,
                3
        )

        val householdRequest = HouseholdRequest(
                municipalityId = ObservableField(1),
                facilityAreaId = ObservableField(1),
                facilityTypeId = ObservableField(1),
                constructionYearId = ObservableField(1),
                electricCarCountId = ObservableField(3),
                occupants = ObservableField(5),
                heatingTypeId = ObservableField(1),
                secondaryHeatingTypeId = ObservableField(2),
                quaternaryHeatingTypeId = ObservableField(4),
                tertiaryHeatingTypeId = ObservableField(3)
        )

        //when
        val json = mapper.toHouseholdRequestJson(householdRequest)

        //then
        assertThat(json).isEqualTo(expected)
    }
}
