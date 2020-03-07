@file:Suppress("KDocMissingDocumentation")

package greenely.greenely.login.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import greenely.greenely.login.ui.LoginActivity

@Module
abstract class LoginModule {
    @ContributesAndroidInjector
    abstract fun contributeLoginActivity(): LoginActivity
}

