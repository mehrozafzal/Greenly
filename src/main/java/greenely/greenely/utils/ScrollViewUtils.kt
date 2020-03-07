package greenely.greenely.utils

import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import android.widget.ScrollView

public fun adjustScrollIfInputHasError(inputBox: TextInputEditText, inputBoxLayout: TextInputLayout, scrollView: ScrollView) {
    inputBox.setOnFocusChangeListener { v, hasFocus ->

        if (!hasFocus) {
            return@setOnFocusChangeListener
        }

        if (inputBoxLayout.error.isNullOrBlank()) {
            return@setOnFocusChangeListener
        }
        scrollView.postDelayed({ scrollView.smoothScrollTo(0, inputBoxLayout.top - 40) }, 200)
    }
}

