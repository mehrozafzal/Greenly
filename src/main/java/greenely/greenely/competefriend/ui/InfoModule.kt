package greenely.greenely.competefriend.ui;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
abstract class InfoModule {
    @ContributesAndroidInjector
    abstract fun contributeInfoActivity(): InfoActivity

}

