package greenely.greenely.main.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import greenely.greenely.competefriend.di.CompeteFriendFragmentModule
import greenely.greenely.feed.di.FeedModule
import greenely.greenely.gamification.achievement.di.AchievementFragmentModule
import greenely.greenely.gamification.di.GamificationFragmentModule
import greenely.greenely.gamification.reward.di.RewardFragmentModule
import greenely.greenely.guidance.di.GuidanceFragmentModule
import greenely.greenely.history.HistoryFragmentModule
import greenely.greenely.home.di.HomeFragmentModule
import greenely.greenely.home.di.HomeModule
import greenely.greenely.main.ui.MainActivity
import greenely.greenely.retail.di.RetailFragmentModule
import greenely.greenely.settings.di.SettingsModule


@Module
abstract class MainModule {

    @ContributesAndroidInjector(
            modules = [
                HomeFragmentModule::class,
                SettingsModule::class,
                HistoryFragmentModule::class,
                FeedModule::class,
                HomeModule::class,
                GuidanceFragmentModule::class,
                HomeModule::class,
                RetailFragmentModule::class,
                CompeteFriendFragmentModule::class,
                GamificationFragmentModule::class,
                AchievementFragmentModule::class,
                RewardFragmentModule::class
            ]
    )
    abstract fun contributeMainActivity(): MainActivity

}
