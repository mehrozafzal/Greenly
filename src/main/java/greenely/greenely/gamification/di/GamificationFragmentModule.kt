package greenely.greenely.gamification.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import greenely.greenely.gamification.ui.GamificationFragment

@Module
abstract class GamificationFragmentModule {
    @ContributesAndroidInjector
    abstract fun contributeGamificationFragment(): GamificationFragment

}