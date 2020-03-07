package greenely.greenely.retail.di

import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import greenely.greenely.retail.ui.*
import greenely.greenely.retail.ui.charts.consumptiondatachart.ConsumptionChartSetupFactory
import greenely.greenely.retail.ui.charts.current_day_pricing_chart.CurrentDayPriceChartSetupFactory
import greenely.greenely.retail.ui.charts.next_day_pricing_chart.NextDayPriceChartSetupFactory
import greenely.greenely.retail.ui.charts.pricedatachart.PriceChartSetupFactory

@Module
abstract class RetailFragmentModule {


    @ContributesAndroidInjector(modules = arrayOf(RetailModule::class))
    abstract fun contributeRetailFragment(): RetailFragment

    @ContributesAndroidInjector()
    abstract fun contributeBecomeCustomerFragment(): BecomeCustomerFragment

    @ContributesAndroidInjector()
    abstract fun contributeRetailStartFragment(): RetailStartFragment

    @ContributesAndroidInjector()
    abstract fun contributeRetailSecondFragment(): RetailSecondFragment

    @ContributesAndroidInjector()
    abstract fun contributeRetailThirdFragment(): RetailThirdFragment

    @ContributesAndroidInjector()
    abstract fun contributeRetailFourthFragment(): RetailStartFourthFragment

    @ContributesAndroidInjector()
    abstract fun contributeRetailComingSoonFragment(): RetailComingSoonFragment

    @ContributesAndroidInjector()
    abstract fun contributeRetailComingSoonFourthFragment(): RetailComingSoonFourthFragment


    @ContributesAndroidInjector()
    abstract fun contributeRetailOnboardingDoneFragment(): RetailOnboardingDoneFragment

    @ContributesAndroidInjector()
    abstract fun contributeErrorFragment(): FailedStateFragment

    @ContributesAndroidInjector()
    abstract fun contributeInvoicesFragment(): RetailInvoicesFragment
}

@Module
class RetailModule {

    @Provides
    fun providePriceChartSetupFactory() = PriceChartSetupFactory()

    @Provides
    fun provideCurrentDayPriceChartSetupFactory() = CurrentDayPriceChartSetupFactory()

    @Provides
    fun provideNextDayPriceChartSetupFactory() = NextDayPriceChartSetupFactory()

    @Provides
    fun provideConsumptionChartSetupFactory() = ConsumptionChartSetupFactory()


}
