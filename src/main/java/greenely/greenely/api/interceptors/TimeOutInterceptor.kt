package greenely.greenely.api.interceptors

import android.text.TextUtils
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.util.concurrent.TimeUnit


/**
 * Add interceptor for the chain
 */

class TimeOutInterceptor : Interceptor {

    private val CONNECT_TIMEOUT = "CONNECT_TIMEOUT"
    private val READ_TIMEOUT = "READ_TIMEOUT"
    private val WRITE_TIMEOUT = "WRITE_TIMEOUT"

    override fun intercept(chain: Interceptor.Chain): Response {

        val request : Request = chain.request()

        //Read default timeout values from OkHttpClient
        var connectTimeout = chain.connectTimeoutMillis()
        var readTimeout = chain.readTimeoutMillis()
        var writeTimeout = chain.writeTimeoutMillis()

        //Read custom timeout values from Api request
        val connectTimeoutNew = request.header(CONNECT_TIMEOUT)
        val readTimeoutNew = request.header(READ_TIMEOUT)
        val writeTimeoutNew = request.header(WRITE_TIMEOUT)

        //If custom timeout values are not empty use them, else keep default values from OkHttpClient
        if (!TextUtils.isEmpty(connectTimeoutNew)) {
            connectTimeout = Integer.valueOf(connectTimeoutNew)
        }
        if (!TextUtils.isEmpty(readTimeoutNew)) {
            readTimeout = Integer.valueOf(readTimeoutNew)
        }
        if (!TextUtils.isEmpty(writeTimeoutNew)) {
            writeTimeout = Integer.valueOf(writeTimeoutNew)
        }

        //We don't need to send these to server
        val builder = request.newBuilder()
        builder.removeHeader(CONNECT_TIMEOUT)
        builder.removeHeader(READ_TIMEOUT)
        builder.removeHeader(WRITE_TIMEOUT)

        return chain
                .withConnectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
                .withReadTimeout(readTimeout, TimeUnit.MILLISECONDS)
                .withWriteTimeout(writeTimeout, TimeUnit.MILLISECONDS)
                .proceed(request)
    }
}

