package greenely.greenely.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import greenely.greenely.GreenelyViewModelFactory
import greenely.greenely.competefriend.ui.CompeteFriendViewModel
import greenely.greenely.feed.ui.FeedViewModel
import greenely.greenely.forgotpassword.ForgotPasswordViewModel
import greenely.greenely.gamification.achievement.ui.AchievementViewModel
import greenely.greenely.gamification.reward.ui.RewardFragmentViewModel
import greenely.greenely.gamification.ui.GamificationViewModel
import greenely.greenely.guidance.ui.GuidanceViewModel
import greenely.greenely.history.HistoryViewModel
import greenely.greenely.home.ui.HomeViewModel
import greenely.greenely.login.ui.LoginViewModel
import greenely.greenely.main.ui.MainViewModel
import greenely.greenely.profile.ui.UpdateProfileViewModel
import greenely.greenely.registration.ui.RegistrationViewModel
import greenely.greenely.retailonboarding.ui.PriceSummaryViewModel
import greenely.greenely.retailonboarding.ui.RedeemCodeViewModel
import greenely.greenely.retail.ui.RetailViewModel
import greenely.greenely.retailinvite.ui.RetailInviteViewModel
import greenely.greenely.retailonboarding.ui.RetailOnboardingViewModel
import greenely.greenely.settings.faq.ui.FaqViewModel
import greenely.greenely.settings.ui.SettingsViewModel
import greenely.greenely.setuphousehold.ui.SetupHouseholdViewModel
import greenely.greenely.solaranalysis.ui.householdinfo.SolarAnalysisViewModel
import greenely.greenely.splash.ui.SplashViewModel
import greenely.greenely.welcome.ui.WelcomeViewModel
import javax.inject.Singleton

@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    abstract fun bindHomeViewModel(viewModel: HomeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(HistoryViewModel::class)
    abstract fun bindHistoryViewModel(viewModel: HistoryViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SettingsViewModel::class)
    abstract fun bindSettingsViewModel(viewModel: SettingsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun bindMainViewModel(viewModel: MainViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FeedViewModel::class)
    abstract fun bindFeedViewModel(viewModel: FeedViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(RegistrationViewModel::class)
    abstract fun bindRegistrationViewModel(viewModel: RegistrationViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel::class)
    abstract fun bindLoginViewModel(loginViewModel: LoginViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ForgotPasswordViewModel::class)
    abstract fun bindForgotPasswordViewModel(forgotPasswordViewModel: ForgotPasswordViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SplashViewModel::class)
    abstract fun bindSplashViewModel(viewModel: SplashViewModel): ViewModel


    @Binds
    @IntoMap
    @ViewModelKey(SolarAnalysisViewModel::class)
    abstract fun bindSolaraAnalysisViewMode(viewModel: SolarAnalysisViewModel): ViewModel


    @Binds
    @IntoMap
    @ViewModelKey(FaqViewModel::class)
    abstract fun bindFaqViewModel(viewModel: FaqViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(greenely.greenely.signature.ui.SignatureViewModel::class)
    abstract fun bindSignatureViewModel2(viewModel: greenely.greenely.signature.ui.SignatureViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(GuidanceViewModel::class)
    abstract fun bindGuidanceViewModel(viewModel: GuidanceViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SetupHouseholdViewModel::class)
    abstract fun bindSetupHouseholdViewModel(viewModel: SetupHouseholdViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(RetailViewModel::class)
    abstract fun bindRetailViewModel(viewModel: RetailViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PriceSummaryViewModel::class)
    abstract fun bindPriceSummaryViewModel(viewModel: PriceSummaryViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(RetailOnboardingViewModel::class)
    abstract fun bindRetailOnboardingViewModel(viewModel: RetailOnboardingViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(RedeemCodeViewModel::class)
    abstract fun bindRedeemCodeViewModel(viewModel: RedeemCodeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(RetailInviteViewModel::class)
    abstract fun bindRetailInviteViewModel(viewModel: RetailInviteViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CompeteFriendViewModel::class)
    abstract fun bindCompeteFriendViewModel(viewModel: CompeteFriendViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(WelcomeViewModel::class)
    abstract fun bindWelcomeViewModel(viewModel: WelcomeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(UpdateProfileViewModel::class)
    abstract fun bindUpdateProfileViewModel(viewModel: UpdateProfileViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(GamificationViewModel::class)
    abstract fun bindGamificationViewModel(viewModel: GamificationViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AchievementViewModel::class)
    abstract fun bindAchievementViewModel(viewModel: AchievementViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(RewardFragmentViewModel::class)
    abstract fun bindRewardViewModel(viewModel: RewardFragmentViewModel): ViewModel

    @Binds
    @Singleton
    abstract fun bindViewModelFactory(factory: GreenelyViewModelFactory): ViewModelProvider.Factory


}

