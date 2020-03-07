package greenely.greenely.di

import dagger.Binds
import dagger.Module
import greenely.greenely.store.SharedPreferencesUserStore
import greenely.greenely.store.UserStore
import greenely.greenely.tracking.MainTracker
import greenely.greenely.tracking.Tracker

@Module
abstract class ApplicationModule {
    @Binds
    abstract fun bindUserStore(userStore: SharedPreferencesUserStore): UserStore

    @Binds
    abstract fun bindTracker(mainTracker: MainTracker): Tracker

}

