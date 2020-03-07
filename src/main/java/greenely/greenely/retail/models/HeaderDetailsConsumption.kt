package greenely.greenely.retail.models

data class HeaderDetailsConsumption(
        val title: String,
        val subTitle: String,
        val value: String,
        val points: List<CurrentMonthPointsJson?>
)
