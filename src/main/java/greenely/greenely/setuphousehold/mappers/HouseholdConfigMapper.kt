package greenely.greenely.setuphousehold.mappers

import greenely.greenely.OpenClassOnDebug
import greenely.greenely.setuphousehold.models.HouseholdConfig
import greenely.greenely.setuphousehold.models.json.HouseholdConfigJsonModel
import javax.inject.Inject

@OpenClassOnDebug
class HouseholdConfigMapper @Inject constructor() {

    fun fromHouseholdConfigJson(json: HouseholdConfigJsonModel): HouseholdConfig {
        return HouseholdConfig(
                introTitle = json.introTitle,
                introText = json.introText,
                municipalities = json.municipalities,
                facilityTypes = json.facilityTypes,
                facilityAreas = json.facilityAreas,
                heatingTypes = json.heatingTypes,
                constructionYears = json.constructionYears,
                occupants = json.occupants,
                electricCarCounts = json.electricCarCounts
        )
    }
}
