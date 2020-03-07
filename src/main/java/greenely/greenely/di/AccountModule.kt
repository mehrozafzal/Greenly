package greenely.greenely.di

import dagger.Module
import dagger.Provides
import greenely.greenely.profile.model.Account
import javax.inject.Singleton

@Module
class AccountModule {
    @Provides
    @Singleton
    fun provideAccountsMap(): HashMap<Int, Account> {
        return HashMap()
    }
}