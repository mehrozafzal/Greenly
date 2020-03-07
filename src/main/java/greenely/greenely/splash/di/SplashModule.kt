@file:Suppress("KDocMissingDocumentation")

package greenely.greenely.splash.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import greenely.greenely.splash.ui.IntroductionActivity
import greenely.greenely.splash.ui.SplashActivity

@Module
abstract class SplashModule {
    @ContributesAndroidInjector
    abstract fun contributeSplashActivity(): SplashActivity

    @ContributesAndroidInjector
    abstract fun contributeIntroductionActivity(): IntroductionActivity
}

