package greenely.greenely.retail.models

import greenely.greenely.R
import greenely.greenely.retailonboarding.ui.util.CombinedPOAFlow
import greenely.greenely.retailonboarding.ui.util.ReferralFlow
import java.io.Serializable

data class RetailOnboardConfig(val monthlyFee: String?,
                               val discount: Float,
                               val onBoardingAllowed: Boolean,
                               var flow: ReferralFlow?) : Serializable {
    companion object {
        const val KEY_CONFIG = "Config"
    }


    fun isCombinedPoaProcess(): Boolean {
        when (flow) {
            is CombinedPOAFlow -> {
                return true
            }
            else -> {
                return false

            }
        }
    }
}

