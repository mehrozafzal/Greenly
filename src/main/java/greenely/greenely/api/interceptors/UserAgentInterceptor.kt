package greenely.greenely.api.interceptors

import greenely.greenely.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response
import java.util.*

/**
 * Class for adding our custom User-Agent to all the api calls
 *
 * @author Anton Holmberg
 */
class UserAgentInterceptor(private val customerId: Int) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        request = request.newBuilder()
                .header(
                        "User-Agent",
                        String.format(Locale.ENGLISH, "Android %d %d", customerId, BuildConfig.VERSION_CODE)
                )
                .method(request.method(), request.body())
                .build()

        return chain.proceed(request)
    }
}
