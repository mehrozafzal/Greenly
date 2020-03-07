package greenely.greenely.retailinvite.models

import com.squareup.moshi.Json

data class ReferralInviteResponseModel(@field:Json(name = "referral_code") val referralCode: String,
                                       @field:Json(name = "remaining_credits") val remainingCredits: Float?,
                                       @field:Json(name = "referral_discount_in_kr") val discount: Float,
                                       @field:Json(name = "share_message") val shareMessage: String
)