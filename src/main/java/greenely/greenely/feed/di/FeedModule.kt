package greenely.greenely.feed.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import greenely.greenely.feed.ui.FeedFragment


@Module
abstract class FeedModule {
    @ContributesAndroidInjector
    abstract fun contributeFeedFragment(): FeedFragment

}
