package greenely.greenely.setuphousehold.models

import androidx.databinding.ObservableField

class HouseholdRequest(
        val municipalityId: ObservableField<Int?> = ObservableField(),
        val facilityTypeId: ObservableField<Int?> = ObservableField(),
        val heatingTypeId: ObservableField<Int?> = ObservableField(),
        val constructionYearId: ObservableField<Int?> = ObservableField(),
        val facilityAreaId: ObservableField<Int?> = ObservableField(),
        val occupants: ObservableField<Int?> = ObservableField(),
        val secondaryHeatingTypeId: ObservableField<Int?> = ObservableField(),
        val quaternaryHeatingTypeId: ObservableField<Int?> = ObservableField(),
        val tertiaryHeatingTypeId: ObservableField<Int?> = ObservableField(),
        val electricCarCountId: ObservableField<Int?> = ObservableField()
)
