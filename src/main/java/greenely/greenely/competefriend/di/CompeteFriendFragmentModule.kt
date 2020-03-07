package greenely.greenely.competefriend.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import greenely.greenely.competefriend.ui.CompeteFriendFragment

@Module
abstract class CompeteFriendFragmentModule {
    @ContributesAndroidInjector
    abstract fun contributeCompeteFriendFragment(): CompeteFriendFragment

}