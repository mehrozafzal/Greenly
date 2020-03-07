package greenely.greenely.api.interceptors

import okhttp3.Interceptor
import okhttp3.Response


/**
 * Add interceptor for the language header.
 */
class LanguageInterceptor : Interceptor {
    /**
     * Add language header to [chain].
     */
    override fun intercept(chain: Interceptor.Chain): Response =
            chain.proceed(
                    chain.request()
                            .newBuilder()
                            .header(
                                    "Accept-Language",
                                    "sv-SE"
                            )
                            .build()
            )
}

