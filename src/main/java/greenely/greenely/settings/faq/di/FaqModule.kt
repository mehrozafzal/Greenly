package greenely.greenely.settings.faq.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import greenely.greenely.settings.faq.ui.FaqActivity

@Module
abstract class FaqModule {
    @ContributesAndroidInjector
    abstract fun contributeFaqActivity(): FaqActivity
}

