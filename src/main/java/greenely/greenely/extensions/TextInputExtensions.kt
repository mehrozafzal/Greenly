package greenely.greenely.extensions

import com.google.android.material.textfield.TextInputLayout
import android.text.Editable
import android.text.TextWatcher

/**
 * Helper for getting the text in a [TextInputLayout].
 */
var TextInputLayout.inputString: String
    get() = editText?.text?.toString() ?: ""
    set(value) {
        editText?.setText(value)
    }

