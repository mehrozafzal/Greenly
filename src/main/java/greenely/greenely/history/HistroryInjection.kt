@file:Suppress("KDocMissingDocumentation")

package greenely.greenely.history

import dagger.Module
import dagger.android.ContributesAndroidInjector
import greenely.greenely.history.views.*
import greenely.greenely.history.views.chart.PriceChartFragment

@Module
abstract class HistoryActivityModule {
    @ContributesAndroidInjector(modules = arrayOf(HistoryFragmentModule::class))
    abstract fun contributeHistoryMonthActivity(): HistoryMonthActivity
}

@Module
abstract class HistoryFragmentModule {
    @ContributesAndroidInjector
    abstract fun contributeHistoryFragment(): HistoryFragment

    @ContributesAndroidInjector
    abstract fun contributeNoContentFragment(): NoContentFragment

    @ContributesAndroidInjector
    abstract fun contributeChartListFragment(): ChartListFragment

    @ContributesAndroidInjector
    abstract fun contributeConsumptionChartFragment(): ConsumptionChartFragment

    @ContributesAndroidInjector
    abstract fun contributeChartHolderFragment(): ChartHolderFragment

    @ContributesAndroidInjector
    abstract fun contributeNavigationChartFragment(): NavigationChartFragment

    @ContributesAndroidInjector
    abstract fun contributeTemperatureChartFragment(): TemperatureChartFragment

    @ContributesAndroidInjector
    abstract fun contributePriceChartFragment(): PriceChartFragment

    @ContributesAndroidInjector
    abstract fun contributeHistoryNotOperationalStateFragment(): HistoryNotOperationalStateFragment

    @ContributesAndroidInjector
    abstract fun contributeDistributionFragment(): DistributionFragment

    @ContributesAndroidInjector
    abstract fun contributeMinMaxFragment(): MinMaxFragment
}

