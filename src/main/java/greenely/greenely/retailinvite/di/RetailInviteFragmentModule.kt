package greenely.greenely.retailinvite.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import greenely.greenely.retailinvite.ui.ReferralInviteQAFragment
import greenely.greenely.retailinvite.ui.RetailInviteFragment

@Module
abstract class RetailInviteFragmentModule {

    @ContributesAndroidInjector()
    abstract fun contributeRetailInviteFragment(): RetailInviteFragment

    @ContributesAndroidInjector()
    abstract fun contributeReferralInviteQAFragment(): ReferralInviteQAFragment

}