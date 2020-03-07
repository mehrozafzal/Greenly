package greenely.greenely.push.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import greenely.greenely.push.NotificationDismissedReceiver
import greenely.greenely.push.PushIntentService
import greenely.greenely.push.PushRegistrationIntentService

@Module
abstract class PushModule {
    @ContributesAndroidInjector
    abstract fun providePushIntentService(): PushIntentService

    @ContributesAndroidInjector
    abstract fun providePushRegistrationIntentService(): PushRegistrationIntentService

    @ContributesAndroidInjector
    abstract fun provideNotificationDismissedReceiver(): NotificationDismissedReceiver
}

