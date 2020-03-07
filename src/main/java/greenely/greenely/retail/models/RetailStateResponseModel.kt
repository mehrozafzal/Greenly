package greenely.greenely.retail.models

import com.github.mikephil.charting.interfaces.datasets.IDataSet
import com.squareup.moshi.Json
import greenely.greenely.retail.data.CustomerState
import greenely.greenely.retail.data.CustomerState.*
import greenely.greenely.retail.data.CustomerStateAdapter
import io.reactivex.internal.schedulers.ImmediateThinScheduler

data class RetailStateResponseModel (
    @field:Json(name = "retail_state") val retailState: String,
    @field:Json(name = "is_retail_customer") val isRetailCustomer: Boolean,
    @field:Json(name = "can_become_retail_customer") val canBecomeRetailCustomer: Boolean,
    @field:Json(name = "referral_discount_in_kr") val discount: Float,
    @field:Json(name = "referral_code") val referralCode: String?

)
{
    fun shouldDisplayPromotionPrompt() = !isRetailCustomer

    fun getCustomerState() = CustomerStateAdapter().fromJson(retailState)

    fun canShowInviteFriends() :Boolean {
        val state=getCustomerState()
        when(state) {
            COMPLETED -> return true
            OPERATIONAL -> return true
            WAITING -> return true
        }
        return false
    }

    fun shouldPromoteRetail() :Boolean {
        val state=getCustomerState()
        var validState=when(state) {
            EMPTY -> return true
            COMPLETED ->  true
            OPERATIONAL ->  true
            WAITING ->  true
            else -> false
        }
        return !isRetailCustomer && validState
    }

}
