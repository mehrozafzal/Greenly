package greenely.greenely.signature.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import greenely.greenely.retailonboarding.di.OnboardingDoneFragmentModule
import greenely.greenely.signature.ui.SignatureActivity
import greenely.greenely.signature.ui.SignatureDoneActivity

@Module
abstract class SignatureModule {
    @ContributesAndroidInjector(modules = [SignatureFragmentModule::class])
    abstract fun contributeSignatureActivity(): SignatureActivity

    @ContributesAndroidInjector(modules = [OnboardingDoneFragmentModule::class])
    abstract fun contributeSignatureDoneActivity(): SignatureDoneActivity
}

