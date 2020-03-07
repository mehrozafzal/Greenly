package greenely.greenely.di

import android.app.Application
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import greenely.greenely.R
import greenely.greenely.api.GreenelyApi
import greenely.greenely.api.interceptors.LanguageInterceptor
import greenely.greenely.api.interceptors.MockInterceptor
import greenely.greenely.api.interceptors.TimeOutInterceptor
import greenely.greenely.api.interceptors.UserAgentInterceptor
import greenely.greenely.home.data.DataStateAdapter
import greenely.greenely.retail.data.CustomerStateAdapter
import greenely.greenely.setuphousehold.mappers.HouseholdInputOptionsMapper
import me.jessyan.retrofiturlmanager.RetrofitUrlManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class NetworkModule {
    @Provides
    @Singleton
    fun provideLanguageInterceptor(): LanguageInterceptor = LanguageInterceptor()

    @Provides
    @Singleton
    fun provideUserAgentInterceptor(application: Application): UserAgentInterceptor =
            UserAgentInterceptor(application.resources.getInteger(R.integer.customer_id))

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    @Provides
    @Singleton
    fun provideTimeOutInterceptor(): TimeOutInterceptor = TimeOutInterceptor()

    @Provides
    @Singleton
    fun providesMockInterceptor(): MockInterceptor =
            MockInterceptor()

    @Provides
    @Singleton
    fun provideOkHttpClient(
            languageInterceptor: LanguageInterceptor,
            userAgentInterceptor: UserAgentInterceptor,
            loggingInterceptor: HttpLoggingInterceptor,
            timeoutInterceptor: TimeOutInterceptor,
            mockInterceptor: MockInterceptor
    ): OkHttpClient =
            RetrofitUrlManager.getInstance().with(OkHttpClient.Builder())
                    .addInterceptor(languageInterceptor)
                    .addInterceptor(userAgentInterceptor)
                    .addInterceptor(loggingInterceptor)
                    .addInterceptor(timeoutInterceptor)
                    .addInterceptor(mockInterceptor)
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(45, TimeUnit.SECONDS)
                    .build()

    @Provides
    @Singleton
    fun provideMoshi() = Moshi.Builder()
            .add(HouseholdInputOptionsMapper())
            .add(CustomerStateAdapter())
            .add(DataStateAdapter())
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(application: Application, client: OkHttpClient, moshi: Moshi) = Retrofit.Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(client)
            .baseUrl(application.getString(R.string.api_base))
            .build()

    @Provides
    @Singleton
    fun provideApi(retrofit: Retrofit): GreenelyApi = retrofit.create(GreenelyApi::class.java)
}
