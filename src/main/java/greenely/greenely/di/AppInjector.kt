package greenely.greenely.di

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.support.AndroidSupportInjectionModule
import greenely.greenely.GreenelyApplication
import greenely.greenely.competefriend.di.CompeteFriendFragmentModule
import greenely.greenely.competefriend.ui.InfoModule
import greenely.greenely.forgotpassword.ForgotPasswordActivityModule
import greenely.greenely.gamification.achievement.di.AchievementFragmentModule
import greenely.greenely.gamification.di.GamificationFragmentModule
import greenely.greenely.gamification.reward.di.RewardFragmentModule
import greenely.greenely.guidance.di.GuidanceModule
import greenely.greenely.history.HistoryActivityModule
import greenely.greenely.login.di.LoginModule
import greenely.greenely.main.di.MainModule
import greenely.greenely.push.di.PushModule
import greenely.greenely.registration.di.RegistrationModule
import greenely.greenely.retailinvite.di.RetailInviteModule
import greenely.greenely.retailonboarding.di.RetailOnboardingModule
import greenely.greenely.settings.faq.di.FaqModule
import greenely.greenely.setuphousehold.di.SetupHouseholdModule
import greenely.greenely.signature.di.SignatureModule
import greenely.greenely.solaranalysis.di.SolarAnalysisModule
import greenely.greenely.splash.di.SplashModule
import greenely.greenely.welcome.di.WelcomeModule
import javax.inject.Singleton

@Component(
        modules = [
            AndroidInjectionModule::class,
            AndroidSupportInjectionModule::class,
            SolarAnalysisModule::class,
            NetworkModule::class,
            ApplicationModule::class,
            LoginModule::class,
            ForgotPasswordActivityModule::class,
            RegistrationModule::class,
            SplashModule::class,
            MainModule::class,
            HistoryActivityModule::class,
            ViewModelModule::class,
            FaqModule::class,
            PushModule::class,
            GuidanceModule::class,
            SignatureModule::class,
            SetupHouseholdModule::class,
            RetailOnboardingModule::class,
            RetailInviteModule::class,
            InfoModule::class,
            WelcomeModule::class,
            CompeteFriendFragmentModule::class,
            GamificationFragmentModule::class,
            AchievementFragmentModule::class,
            RewardFragmentModule::class,
            AccountModule::class
        ]
)
@Singleton
interface AppInjector {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppInjector
    }

    fun inject(app: GreenelyApplication)
}