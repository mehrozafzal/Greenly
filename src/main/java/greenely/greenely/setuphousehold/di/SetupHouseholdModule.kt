package greenely.greenely.setuphousehold.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import greenely.greenely.setuphousehold.ui.SetupHouseholdActivity
import greenely.greenely.setuphousehold.ui.steps.*

@Module
abstract class SetupHouseholdModule {
    @ContributesAndroidInjector(
            modules = arrayOf(
                    SetupHouseholdFragmentModule::class
            ))
    abstract fun contributeSetupHouseholdActivity(): SetupHouseholdActivity

}

@Module
abstract class SetupHouseholdFragmentModule {

    @ContributesAndroidInjector
    abstract fun contributeIntroStep(): IntroStep

    @ContributesAndroidInjector
    abstract fun contributeMuncipalityStep(): MunicipalityStep

    @ContributesAndroidInjector
    abstract fun contributeFacilityTypeStep(): FacilityTypeStep

    @ContributesAndroidInjector
    abstract fun contributePrimaryHeatingTypeStep(): PrimaryHeatingTypeStep

    @ContributesAndroidInjector
    abstract fun contributeFacilityAreaStep(): FacilityAreaStep

    @ContributesAndroidInjector
    abstract fun contributeConstructionYearStep(): ConstructionYearStep

    @ContributesAndroidInjector
    abstract fun contributeElectricCarsStep(): ElectricCarsStep

    @ContributesAndroidInjector
    abstract fun contributeOccupantsStep(): OccupantsStep

    @ContributesAndroidInjector
    abstract fun contributeSecondaryHeatingTypeStep(): SecondaryHeatingTypeStep

}


