package greenely.greenely.utils

import android.app.Application
import greenely.greenely.R
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import javax.inject.Inject


class CurrencyFormatUtils @Inject constructor(private val application: Application){
    fun currencyFormatter(value: Int): String {
        val format = NumberFormat.getCurrencyInstance()
        val symbol = DecimalFormatSymbols()
        symbol.currencySymbol = ""
        (format as DecimalFormat).decimalFormatSymbols = symbol
        format.minimumFractionDigits = 0
        return format.format(value) + " " + application.getString(R.string.currency_symbol)
    }
}