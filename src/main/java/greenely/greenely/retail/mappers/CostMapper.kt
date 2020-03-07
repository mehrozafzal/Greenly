package greenely.greenely.retail.mappers

import java.text.DecimalFormat
import javax.inject.Inject

class CostMapper @Inject constructor() {
    fun formatCost(value: Int?, decimalPlaces: Int): String {
        val formatter = DecimalFormat()
        when (decimalPlaces) {
            2 -> {
                value?.let {
                    formatter.applyPattern("###,###,##0.00")
                    return formatter.format(value.div(100.0))
                }
                return "--"
            }
            else -> {
                value?.let {
                    formatter.applyPattern("###,###,##0")
                    return formatter.format(value.div(100.0))
                }
                return "--"
            }

        }

    }
}

