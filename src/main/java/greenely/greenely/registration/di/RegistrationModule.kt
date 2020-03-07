@file:Suppress("KDocMissingDocumentation")

package greenely.greenely.registration.di

import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import greenely.greenely.profile.model.Account
import greenely.greenely.registration.ui.RegistrationActivity
import javax.inject.Singleton

@Module
abstract class RegistrationModule {
    @ContributesAndroidInjector
    abstract fun contributeRegistrationActivity(): RegistrationActivity


}
