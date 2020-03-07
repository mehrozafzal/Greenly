package greenely.greenely.extensions

import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup

/**
 * Set the data for a [RadioGroup].
 *
 * @param T the type the list data.
 * @param data The data that should be shown in the [RadioGroup].
 * @param trans The transforming function that creates views from [data].
 */
fun <T> RadioGroup.setData(data: List<T>, trans: RadioGroup.(T) -> Pair<RadioButton, ViewGroup.LayoutParams>) {
    this.removeAllViews()
    data.forEach {
        val (rb, lp) = trans(it)
        this.addView(rb, lp)
    }
}


/**
 * Function to select a [RadioButton] in a [RadioGroup] based on [id]
 *
 * @param id The id of the [RadioButton] that should be selected.
 */
fun RadioGroup.selectById(id: Int) {
    val button: RadioButton? = findViewById(id)
    button?.apply {
        isChecked = true
    }
}
