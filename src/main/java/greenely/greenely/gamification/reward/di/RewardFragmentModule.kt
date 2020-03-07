package greenely.greenely.gamification.reward.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import greenely.greenely.gamification.reward.ui.RewardFragment

@Module
abstract class RewardFragmentModule {
    @ContributesAndroidInjector
    abstract fun contributeRewardFragment(): RewardFragment

}