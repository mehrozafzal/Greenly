package greenely.greenely.forgotpassword

import com.squareup.moshi.Json
import greenely.greenely.OpenClassOnDebug
import greenely.greenely.api.GreenelyApi
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manager for forgot password data
 */

data class ForgotPasswordRequest(@field:Json(name = "recovery") val email: String)

/**
 * Implementation of [ForgotPasswordRepo] using the api as the model layer.
 */
@OpenClassOnDebug
@Singleton
class ForgotPasswordRepo @Inject constructor(private val api: GreenelyApi) {

    fun sendEmail(email: String): Completable =
            api.sendEmail(ForgotPasswordRequest(email))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
}