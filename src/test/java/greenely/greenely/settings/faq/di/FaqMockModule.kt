package greenely.greenely.settings.faq.di

import com.nhaarman.mockito_kotlin.mock
import dagger.Module
import dagger.Provides
import greenely.greenely.settings.faq.ui.events.EventHandler

@Module
class FaqMockModule {
    @Provides
    fun provideEventHandler(): EventHandler = mock()
}

