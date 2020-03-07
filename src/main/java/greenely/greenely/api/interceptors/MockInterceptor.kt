package greenely.greenely.api.interceptors

import greenely.greenely.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Class for adding our custom Mock API KEY interceptor
 *
 * @author Eimantas Urbonas
 */
class MockInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        request = request.newBuilder()
                .header(
                        BuildConfig.postman_app_id,
                        BuildConfig.postman_api_key
                )
                .method(request.method(), request.body())
                .build()

        return chain.proceed(request)
    }
}
