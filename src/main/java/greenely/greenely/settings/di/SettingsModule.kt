package greenely.greenely.settings.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import greenely.greenely.profile.ui.UpdateProfileFragment
import greenely.greenely.settings.ui.*

@Module
abstract class SettingsModule {

    @ContributesAndroidInjector
    abstract fun provideSettingsFragment(): SettingsFragment


    @ContributesAndroidInjector
    abstract fun provideHouseholdSettingsFragment(): HouseholdSettingsFragment

    @ContributesAndroidInjector
    abstract fun provideNotificationSettingsFragment(): NotificationSettingsFragment

    @ContributesAndroidInjector
    abstract fun provideChangePasswordFragment(): ChangePasswordFragment

    @ContributesAndroidInjector
    abstract fun provideUpdateProfileFragment(): UpdateProfileFragment

}

