package greenely.greenely.setuphousehold.models

data class HouseholdConfig(
        val introTitle: String,
        val introText: String,
        val municipalities: List<HouseholdInputOptions>,
        val facilityTypes: List<HouseholdInputOptions>,
        val heatingTypes: List<HouseholdInputOptions>,
        val constructionYears: List<HouseholdInputOptions>,
        val facilityAreas: List<HouseholdInputOptions>,
        val occupants: List<HouseholdInputOptions>,
        val electricCarCounts: List<HouseholdInputOptions>
)