package greenely.greenely.retail.models

data class HeaderDetailsPricing(
        val title: String,
        val subTitle: String,
        val value: String,
        val points: List<PricingPointsJson?>
)
