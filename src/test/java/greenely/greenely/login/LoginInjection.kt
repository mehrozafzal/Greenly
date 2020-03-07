@file:Suppress("KDocMissingDocumentation")

package greenely.greenely.login

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import dagger.Module
import dagger.Provides
import greenely.greenely.accountsetup.AccountSetupNextHandler
import greenely.greenely.login.ui.LoginErrorHandler
import greenely.greenely.utils.LoadingHandler
import greenely.greenely.utils.OverlappingLoaderFactory

@Module
class LoginMockModule {

    @Provides
    fun provideNavigationHandler(): AccountSetupNextHandler = mock()

    @Provides
    fun provideErrorHandler(): LoginErrorHandler = mock()

    @Provides
    fun providesLoadingHandlerFactory(): OverlappingLoaderFactory {
        val loadingHandler = mock<LoadingHandler>()
        return mock {
            on { createLoadingHandler(any(), any()) } doReturn loadingHandler
        }
    }
}

