package greenely.greenely.api.models

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import retrofit2.HttpException

data class ErrorMessage(val title: String, val description: String) {
    companion object {
        private val adapter: JsonAdapter<ErrorMessage> by lazy {
            val moshi = Moshi.Builder()
                    .build()
            moshi.adapter(ErrorMessage::class.java)
        }

        fun fromError(exception: HttpException): ErrorMessage? =
                exception.response().errorBody()?.string()?.let {
                    adapter.fromJson(it)
                }
    }
}

