package greenely.greenely.gamification.achievement.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import greenely.greenely.gamification.achievement.ui.AchievementFragment

@Module
abstract class AchievementFragmentModule {
    @ContributesAndroidInjector
    abstract fun contributeAchievementFragment(): AchievementFragment

}