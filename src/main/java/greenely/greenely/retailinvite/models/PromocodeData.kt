package greenely.greenely.retailinvite.models

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.squareup.moshi.Json
import greenely.greenely.BR
import greenely.greenely.OpenClassOnDebug


@OpenClassOnDebug
class PromocodeData(promocode: String = "") : BaseObservable() {
    /**
     * Email of the user.
     */
    @Bindable
    @field:Json(name = "promocode")
    var promocode = promocode
        set(value) {
            field = value
            notifyPropertyChanged(BR.promocode)
        }
}
