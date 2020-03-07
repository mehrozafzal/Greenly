@file:Suppress("KDocMissingDocumentation")

package greenely.greenely.splash.di

import com.nhaarman.mockito_kotlin.mock
import dagger.Module
import dagger.Provides
import greenely.greenely.accountsetup.AccountSetupNextHandler
import greenely.greenely.splash.ui.navigation.SplashNavigationHandler

@Module
class SplashMockModule {

    @Provides
    fun provideNavigationHandler(): SplashNavigationHandler = mock()

    @Provides
    fun provideAccountSetupNextHandler(): AccountSetupNextHandler = mock()
}

