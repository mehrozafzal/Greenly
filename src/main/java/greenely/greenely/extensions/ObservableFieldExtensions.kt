package greenely.greenely.extensions

import androidx.databinding.Observable

/**
 * Helper function for adding an [Observable.OnPropertyChangedCallback].
 *
 * @param callback The callback function to be called when the property is changed.
 */
inline fun Observable.onPropertyChanged(crossinline callback: Observable.() -> Unit) {
    this.addOnPropertyChangedCallback(
            object : Observable.OnPropertyChangedCallback() {
                /** Called when a property is changed. */
                override fun onPropertyChanged(p0: androidx.databinding.Observable?, p1: Int) {
                    callback()
                }
            }
    )
}

