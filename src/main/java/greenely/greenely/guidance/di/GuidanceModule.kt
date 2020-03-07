package greenely.greenely.guidance.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import greenely.greenely.guidance.ui.ArticleDetailActivity
import greenely.greenely.guidance.ui.GuidanceFragment
import greenely.greenely.guidance.ui.OfferDetailActivity
import greenely.greenely.guidance.ui.TipsDetailActivity
import greenely.greenely.guidance.ui.latestsolaranalysis.LatestSolarAnalysisActivity

@Module
abstract class GuidanceModule {
    @ContributesAndroidInjector
    abstract fun contributeOfferDetailActivity(): OfferDetailActivity

    @ContributesAndroidInjector
    abstract fun contributeArticleDetailActivity(): ArticleDetailActivity

    @ContributesAndroidInjector
    abstract fun contributeTipsDeatilActivity(): TipsDetailActivity

    @ContributesAndroidInjector
    abstract fun contributeLatestSolarAnalysisActivity(): LatestSolarAnalysisActivity
}

@Module
abstract class GuidanceFragmentModule {
    @ContributesAndroidInjector
    abstract fun contributeGuidanceFragment(): GuidanceFragment

}

