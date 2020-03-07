package greenely.greenely.retailonboarding.ui.util

import java.io.Serializable
sealed class ReferralFlow(var promoCode:String?) :Serializable
class PriceSummaryFlow(var promo:String?) : ReferralFlow(promo)
class RedeemCodeFlow(var promo:String?) : ReferralFlow(promo)
class CombinedPOAFlow(var promo:String?,val personalNumber:String) : ReferralFlow(promo)