@file:Suppress("KDocMissingDocumentation")

package greenely.greenely.registration.di

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import dagger.Module
import dagger.Provides
import greenely.greenely.registration.ui.RegistrationErrorHandler
import greenely.greenely.registration.ui.validation.RegistrationInputValidator
import greenely.greenely.utils.LoadingHandler
import greenely.greenely.utils.OverlappingLoaderFactory

@Module
class RegistrationMockModule {
    @Provides
    fun provideRegistrationInputValidator(): RegistrationInputValidator = mock()

    @Provides
    fun provideErrorHandler(): RegistrationErrorHandler = mock()

    @Provides
    fun provideLoaderFactory(): OverlappingLoaderFactory {
        val loadingHandler = mock<LoadingHandler>()
        return mock {
            on { createLoadingHandler(any(), any()) } doReturn loadingHandler
        }
    }
}

