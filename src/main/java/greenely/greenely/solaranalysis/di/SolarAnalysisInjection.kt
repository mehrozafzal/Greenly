package greenely.greenely.solaranalysis.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import greenely.greenely.solaranalysis.data.SendContactInfoIntentService
import greenely.greenely.solaranalysis.ui.householdinfo.SolarAnalysisActivity
import greenely.greenely.solaranalysis.ui.householdinfo.steps.*

@Module
abstract class SolarAnalysisFragmentModule {
    @ContributesAndroidInjector
    abstract fun contributeAddressStep(): AddressStep

    @ContributesAndroidInjector
    abstract fun contributeRoofAndleStep(): RoofAngleStep

    @ContributesAndroidInjector
    abstract fun contributeRoofDirectionStep(): RoofDirectionStep

    @ContributesAndroidInjector
    abstract fun contributeRoofSizeStep(): RoofSizeStep

    @ContributesAndroidInjector
    abstract fun contributeResultStep(): ResultStep

    @ContributesAndroidInjector
    abstract fun contributeContactInfoStep(): ContactInfoStep
}

@Module
abstract class SolarAnalysisModule {
    @ContributesAndroidInjector(modules = arrayOf(SolarAnalysisFragmentModule::class))
    abstract fun contributeSolarAnalysisActivity(): SolarAnalysisActivity


    @ContributesAndroidInjector
    abstract fun contributeSendContactInfoIntentService(): SendContactInfoIntentService
}

