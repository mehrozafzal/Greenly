@file:Suppress("KDocMissingDocumentation", "unused")

package greenely.greenely.di

import androidx.lifecycle.ViewModelProvider
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import dagger.android.AndroidInjectionModule
import dagger.android.ContributesAndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import greenely.greenely.EmptyActivity
import greenely.greenely.TestApplication
import greenely.greenely.feed.ui.FeedFragment
import greenely.greenely.login.LoginMockModule
import greenely.greenely.login.ui.LoginActivity
import greenely.greenely.registration.di.RegistrationMockModule
import greenely.greenely.registration.ui.RegistrationActivity
import greenely.greenely.settings.faq.di.FaqMockModule
import greenely.greenely.settings.faq.ui.FaqActivity
import greenely.greenely.solaranalysis.data.SendContactInfoIntentService
import greenely.greenely.solaranalysis.di.SolarAnalysisMockModule
import greenely.greenely.solaranalysis.ui.householdinfo.SolarAnalysisActivity
import greenely.greenely.solaranalysis.ui.householdinfo.SolarAnalysisErrorHandler
import greenely.greenely.solaranalysis.ui.householdinfo.events.EventHandler
import greenely.greenely.solaranalysis.ui.householdinfo.events.EventHandlerFactory
import greenely.greenely.solaranalysis.ui.householdinfo.steps.*
import greenely.greenely.splash.di.SplashMockModule
import greenely.greenely.splash.ui.SplashActivity
import greenely.greenely.tracking.Tracker
import javax.inject.Singleton

@Module
abstract class FragmentModule {
    @ContributesAndroidInjector
    abstract fun contributeFeedFragment(): FeedFragment

    @ContributesAndroidInjector
    abstract fun contributeAddressStep(): AddressStep

    @ContributesAndroidInjector
    abstract fun contributeRoofSizeStep(): RoofSizeStep

    @ContributesAndroidInjector
    abstract fun contributeRoofAngleStep(): RoofAngleStep

    @ContributesAndroidInjector
    abstract fun contributeRoofDirectionStep(): RoofDirectionStep

    @ContributesAndroidInjector
    abstract fun contributeResultStep(): ResultStep

    @ContributesAndroidInjector
    abstract fun contributeContactInfoStep(): ContactInfoStep
}

@Module
abstract class ActivityModule {
    @ContributesAndroidInjector(modules = [FragmentModule::class])
    abstract fun contributeEmptyActivity(): EmptyActivity

    @Singleton
    @ContributesAndroidInjector(modules = [RegistrationMockModule::class])
    abstract fun contributeRegistrationActivity(): RegistrationActivity

    @ContributesAndroidInjector(modules = [LoginMockModule::class])
    abstract fun contributeLoginActivity(): LoginActivity

    @ContributesAndroidInjector(modules = [SplashMockModule::class])
    abstract fun contributeSplashActivity(): SplashActivity

    @ContributesAndroidInjector(modules = [FragmentModule::class])
    abstract fun contributeSolarAnalysisActivity(): SolarAnalysisActivity

    @ContributesAndroidInjector(modules = [FaqMockModule::class])
    abstract fun contributeFaqActivity(): FaqActivity
}

@Module
abstract class AndroidServiceModule {
    @ContributesAndroidInjector(modules = [SolarAnalysisMockModule::class])
    abstract fun contributeSendContactInfoIntentService(): SendContactInfoIntentService
}

@Module
class ServiceModule {
    @Provides
    fun provideTracker(): Tracker = mock()

    @Provides
    fun provideEventHandlerFactory(): EventHandlerFactory = mock {
        on { createEventHandler(any()) } doReturn mock<EventHandler>()
    }

    @Provides
    fun provideSolarAnalysisErrorHandler(): SolarAnalysisErrorHandler = mock()
}

@Component(
        modules = [
        AndroidInjectionModule::class,
        AndroidSupportInjectionModule::class,
        AndroidServiceModule::class,
        ServiceModule::class,
        ActivityModule::class
        ]
)
interface TestComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun viewModelProviderFactory(provider: ViewModelProvider.Factory): Builder

        fun build(): TestComponent
    }

    fun inject(application: TestApplication)
}
