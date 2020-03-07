package greenely.greenely.retailonboarding.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import greenely.greenely.retailonboarding.steps.PriceSummaryFragment
import greenely.greenely.retail.ui.RetailOnboardingDoneFragment
import greenely.greenely.retailonboarding.steps.RetailRedeemCodeFragment
import greenely.greenely.retailonboarding.steps.AddressStepFragment
import greenely.greenely.retailonboarding.steps.ContactInformationStepFragment
import greenely.greenely.retailonboarding.ui.CombinedPOAAdressStepFragment

@Module
abstract class RetailOnboardingFragmentModule {

    @ContributesAndroidInjector
    abstract fun contributeAddressStepFragment(): AddressStepFragment

    @ContributesAndroidInjector
    abstract fun contributeContactInformationStepFragment(): ContactInformationStepFragment

    @ContributesAndroidInjector
    abstract fun contributeRetailOnboardingDoneFragment(): RetailOnboardingDoneFragment

    @ContributesAndroidInjector()
    abstract fun contributeRetailInfoFragment(): PriceSummaryFragment

    @ContributesAndroidInjector()
    abstract fun contributeRetailRedeemCodeFragment(): RetailRedeemCodeFragment

    @ContributesAndroidInjector()
    abstract fun contributeCombinedPOAAdressStepFragment(): CombinedPOAAdressStepFragment




}