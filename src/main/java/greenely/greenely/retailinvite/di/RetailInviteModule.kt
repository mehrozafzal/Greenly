package greenely.greenely.retailinvite.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import greenely.greenely.retailinvite.ui.RetaiInviteActivity

@Module
abstract class RetailInviteModule {

    @ContributesAndroidInjector(modules = [RetailInviteFragmentModule::class])
    abstract fun contributeRetailInviteActivity(): RetaiInviteActivity
}