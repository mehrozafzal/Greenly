@file:Suppress("KDocMissingDocumentation")

package greenely.greenely.forgotpassword

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ForgotPasswordActivityModule {
    @ContributesAndroidInjector
    abstract fun contributeForgotPasswordActivity(): ForgotPasswordActivity
}
