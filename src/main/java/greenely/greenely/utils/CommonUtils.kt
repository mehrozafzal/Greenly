package greenely.greenely.utils

import greenely.greenely.home.models.capitalizeWords
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

class CommonUtils {

    companion object {

        private fun isPerfectSquare(x: Int): Boolean {
            val s = Math.sqrt(x.toDouble()).toInt()
            return s * s == x
        }

        // Returns true if n is a Fibonacci Number, else false
        fun isFibonacci(n: Int): Boolean {
            return isPerfectSquare(5 * n * n + 4) || isPerfectSquare(5 * n * n - 4)
        }

        fun getCurrencyFormat(value: Float): String {
            val svSE = DecimalFormat()
            val symbols = DecimalFormatSymbols(Locale("sv", "SE"))
            symbols.setDecimalSeparator(',')
            symbols.setGroupingSeparator(' ')
            svSE.maximumFractionDigits = 2
            svSE.decimalFormatSymbols = symbols
            return svSE.format(value)
        }
    }
}

fun String.formatDate(): String {
    return this.replace(".", "").capitalizeWords()
}