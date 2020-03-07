package greenely.greenely.solaranalysis.di

import com.nhaarman.mockito_kotlin.mock
import dagger.Module
import dagger.Provides
import greenely.greenely.solaranalysis.data.SolarAnalysisRepo
import greenely.greenely.solaranalysis.ui.householdinfo.SolarAnalysisActivity
import greenely.greenely.solaranalysis.ui.householdinfo.charting.ChartManager


@Module
class SolarAnalysisMockModule {
    @Provides
    fun providerChartManager(): ChartManager = mock()

    @Provides
    fun provideRepo(): SolarAnalysisRepo = mock()

    @Provides
    fun provideSoolarAnalysisHousdeholdInfoActivity(): SolarAnalysisActivity = mock()

}
