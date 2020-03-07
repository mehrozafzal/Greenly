package greenely.greenely.utils

import io.reactivex.internal.operators.flowable.FlowableTake
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

/**
 * Convert a float representing consumption to a string with the appropriate number of digits.
 */
fun Float.consumptionToString(): String =
        when {
            this > 100.0f -> "%d".format(Math.round(this))
            this > 10.0f -> DecimalFormat("00.0").format(this)
            else -> DecimalFormat("0.00").format(this)
        }


fun Float.getCurrencyFormat():String{
    return this.getCurrencyFormatUptoDecimalPlace(2)
}

fun DateTime.getMonthAndYear():String{

    return  DateTimeFormat.forPattern("MMMM yyyy").withLocale(Locale("en", "US")).print(this).capitalize()
}

fun Float.getCurrencyFormatUptoDecimalPlace(numberOfDecimal:Int) :String
{
    val svSE = DecimalFormat()
    val symbols = DecimalFormatSymbols(Locale("sv", "SE"))
    symbols.setDecimalSeparator(',')
    symbols.setGroupingSeparator(' ')
    svSE.maximumFractionDigits=numberOfDecimal
    svSE.decimalFormatSymbols = symbols
    return svSE.format(this)
}




fun Float.toCurrenyWithDecimalCheck(): String =
        when {
        this > 100.0f -> "%d".format(Math.round(this)).toFloat().getCurrencyFormat()
        this > 10.0f -> this.getCurrencyFormatUptoDecimalPlace(1)
        else -> this.getCurrencyFormat()
    }




fun Float.toCurrenyWithDecimalCheckAndSymbol(): String = this.consumptionToString()+" kWh"


private fun isPerfectSquare(x: Int): Boolean {
    val s = Math.sqrt(x.toDouble()).toInt()
    return s * s == x
}

// Returns true if n is a Fibonacci Number, else false
fun isFibonacci(n: Int): Boolean {
    return isPerfectSquare(5 * n * n + 4) || isPerfectSquare(5 * n * n - 4)
}







