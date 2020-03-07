package greenely.greenely.retailonboarding.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import greenely.greenely.retailonboarding.steps.AddressStepFragment
import greenely.greenely.retailonboarding.ui.OnboardCompletedFragment

@Module
abstract class OnboardingDoneFragmentModule {

    @ContributesAndroidInjector
    abstract fun contributeOnboardCompletedFragment(): OnboardCompletedFragment
}