@file:Suppress("KDocMissingDocumentation")

package greenely.greenely.home.di

import android.app.Application
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.IValueFormatter
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import greenely.greenely.home.data.HomeModelFactory
import greenely.greenely.home.ui.HomeFragment
import greenely.greenely.home.ui.historicalcomparison.HistoricalComparisonChartSetupFactory
import greenely.greenely.home.ui.historicalcomparison.XAxisValueFormatterFactory
import greenely.greenely.home.ui.historicalcomparison.YAxisValueFormatterFactory
import greenely.greenely.home.ui.latestcomparison.LatestComparisonChartSetupFactory
import greenely.greenely.home.ui.latestcomparison.XAxisValueFormatter

/**
 * Module for the component list fragment.
 */

@Module
class HomeModule {
    @Provides
    fun provideHomeModelFactory(context: Application) = HomeModelFactory(context)

    @Provides
    fun provideLatestChartChainFactory(
            valueFormatter: XAxisValueFormatter
    ): LatestComparisonChartSetupFactory = LatestComparisonChartSetupFactory(valueFormatter)

    @Provides
    fun provideValueFormatter(application: Application): XAxisValueFormatter = XAxisValueFormatter(application)


    @Provides
    fun provideHistoricalComparisonChartChainFactory(
            valueFormatter: YAxisValueFormatterFactory,
            formatterFactory: XAxisValueFormatterFactory
    ) =
            HistoricalComparisonChartSetupFactory(valueFormatter, formatterFactory)

    @Provides
    fun provideFormatterFactory() = XAxisValueFormatterFactory()


}

/**
 * Module for injecting all the fragments contained in home.
 */
@Module
abstract class HomeFragmentModule {

    /**
     * Contributes a [HomeFragment] to the android injector.
     */
    @ContributesAndroidInjector(modules = arrayOf(HomeModule::class))
    abstract fun contributeHomeFragment(): HomeFragment

}

