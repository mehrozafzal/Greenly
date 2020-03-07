package greenely.greenely.setuphousehold.mappers

import greenely.greenely.OpenClassOnDebug
import greenely.greenely.setuphousehold.models.HouseholdRequest
import greenely.greenely.setuphousehold.models.json.HouseholdRequestJsonModel
import javax.inject.Inject

@OpenClassOnDebug
class HouseholdRequestMapper @Inject constructor() {

    fun toHouseholdRequestJson(householdRequest: HouseholdRequest): HouseholdRequestJsonModel {
        return HouseholdRequestJsonModel(
                municipalityId = householdRequest.municipalityId.get(),
                facilityTypeId = householdRequest.facilityTypeId.get(),
                facilityAreaId = householdRequest.facilityAreaId.get(),
                constructionYearId = householdRequest.constructionYearId.get(),
                electricCarCountId = householdRequest.electricCarCountId.get(),
                occupants = householdRequest.occupants.get(),
                heatingTypeId = householdRequest.heatingTypeId.get(),
                secondaryHeatingTypeId = householdRequest.secondaryHeatingTypeId.get(),
                quaternaryHeatingTypeId = householdRequest.quaternaryHeatingTypeId.get(),
                tertiaryHeatingTypeId = householdRequest.tertiaryHeatingTypeId.get()
        )
    }
}