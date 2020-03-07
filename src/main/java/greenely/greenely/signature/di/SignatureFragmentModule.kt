package greenely.greenely.signature.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import greenely.greenely.signature.ui.steps.AddressStep
import greenely.greenely.signature.ui.steps.confirmation_step.ConfirmationStep
import greenely.greenely.signature.ui.steps.PersonalNumberStep
import greenely.greenely.signature.ui.steps.SigningStep

@Module
abstract class SignatureFragmentModule {
    @ContributesAndroidInjector
    abstract fun contributePersonalNumberStep(): PersonalNumberStep

    @ContributesAndroidInjector
    abstract fun contributeSignStep(): SigningStep

    @ContributesAndroidInjector
    abstract fun contributeAddressStep(): AddressStep

    @ContributesAndroidInjector
    abstract fun contributeConfirmationStep(): ConfirmationStep
}