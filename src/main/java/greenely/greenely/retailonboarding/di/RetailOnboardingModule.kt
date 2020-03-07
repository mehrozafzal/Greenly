package greenely.greenely.retailonboarding.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import greenely.greenely.retailonboarding.ui.RetailOnboardingActivity

@Module
abstract class RetailOnboardingModule {
    @ContributesAndroidInjector(modules = [RetailOnboardingFragmentModule::class,OnboardingDoneFragmentModule::class])
    abstract fun contributeRetailOnboardingActivity(): RetailOnboardingActivity


}
