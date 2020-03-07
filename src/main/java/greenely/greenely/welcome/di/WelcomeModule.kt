@file:Suppress("KDocMissingDocumentation")

package greenely.greenely.welcome.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import greenely.greenely.welcome.ui.WelcomeActivity

@Module
abstract class WelcomeModule {
    @ContributesAndroidInjector
    abstract fun contributeWelcomeActivity(): WelcomeActivity
}
