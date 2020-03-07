package greenely.greenely.login.model

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.squareup.moshi.Json
import greenely.greenely.BR
import greenely.greenely.OpenClassOnDebug

/**
 * Model for the login data.
 */
@OpenClassOnDebug
class LoginData(email: String = "", password: String = "") : BaseObservable() {
    /**
     * Email of the user.
     */
    @Bindable
    @field:Json(name = "email")
    var email = email
        set(value) {
            field = value
            notifyPropertyChanged(BR.email)
        }

    /**
     * Password of the user.
     */
    @Bindable
    @field:Json(name = "password")
    var password = password
        set(value) {
            field = value
            notifyPropertyChanged(BR.password)
        }

    /**
     * Equality check.
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LoginData

        if (email != other.email) return false
        if (password != other.password) return false

        return true
    }

    /**
     * Hash code generation.
     */
    override fun hashCode(): Int {
        var result = email.hashCode()
        result = 31 * result + password.hashCode()
        return result
    }


}

