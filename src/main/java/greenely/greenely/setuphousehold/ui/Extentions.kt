package greenely.greenely.setuphousehold.ui

import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import greenely.greenely.extensions.dp
import greenely.greenely.extensions.setData
import greenely.greenely.setuphousehold.models.HouseholdInputOptions

internal fun RadioGroup.setInputOptions(options: List<HouseholdInputOptions>, checkedInt: Int) {
    setData(options) {
        RadioButton(context).apply {
            id = it.id
            text = it.name
            setPadding(
                    paddingLeft, context.dp(16).toInt(), paddingRight, context.dp(16).toInt()
            )
        }.apply {
            isChecked = checkedInt > -1 && it.id == checkedInt
        } to RadioGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            topMargin = context.dp(8).toInt()
        }
    }
}



