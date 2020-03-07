package greenely.greenely.extensions

import android.content.Context
import android.util.TypedValue

/**
 * Get pixels representation of dp.
 *
 * @param dp The number of dps.
 * @return The pixel equivalent of [dp].
 *
 * @see TypedValue.COMPLEX_UNIT_DIP
 */
fun Context.dp(dp: Int): Float = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), resources.displayMetrics
)

/**
 * Get pixel representation of sp.
 *
 * @param sp The number of sps.
 * @return The pixel representation of [sp].
 *
 * @see TypedValue.COMPLEX_UNIT_SP
 */
fun Context.sp(sp: Int) = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP, sp.toFloat(), resources.displayMetrics
)

