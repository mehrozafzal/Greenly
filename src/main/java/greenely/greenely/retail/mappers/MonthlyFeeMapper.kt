package greenely.greenely.retail.mappers

import android.app.Application
import greenely.greenely.R
import javax.inject.Inject

class MonthlyFeeMapper @Inject constructor(
        application: Application
) {
    private val currencyFormat = application.getString(R.string.kr_per_month)

    fun fromMonthlyFeeJson(value: Int): String {
        return currencyFormat.format(value)
    }
}

