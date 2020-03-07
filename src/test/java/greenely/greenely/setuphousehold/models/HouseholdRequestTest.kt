package greenely.greenely.setuphousehold.models

import androidx.databinding.ObservableField
import org.junit.Assert.*
import org.junit.Test

class HouseholdRequestTest{

    @Test
    fun testHouseholdRequest(){

        val model = HouseholdRequest(
                municipalityId = ObservableField(1),
                facilityAreaId = ObservableField(1),
                facilityTypeId = ObservableField(1),
                constructionYearId = ObservableField(1),
                electricCarCountId = ObservableField(1),
                occupants = ObservableField(1),
                heatingTypeId = ObservableField(1),
                secondaryHeatingTypeId = ObservableField(1),
                quaternaryHeatingTypeId = ObservableField(1),
                tertiaryHeatingTypeId = ObservableField(1)
        )

        assertEquals(1, model.constructionYearId.get())
        assertEquals(1, model.municipalityId.get())
        assertEquals(1, model.facilityAreaId.get())
        assertEquals(1, model.facilityTypeId.get())
        assertEquals(1, model.electricCarCountId.get())
        assertEquals(1, model.occupants.get())
        assertEquals(1, model.heatingTypeId.get())
        assertEquals(1, model.secondaryHeatingTypeId.get())
        assertEquals(1, model.quaternaryHeatingTypeId.get())
        assertEquals(1, model.tertiaryHeatingTypeId.get())

    }
}
