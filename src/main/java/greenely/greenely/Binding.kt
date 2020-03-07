package greenely.greenely

import android.content.Intent
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.databinding.adapters.ListenerUtil
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import android.text.Html
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import greenely.greenely.history.views.CircleChartView
import greenely.greenely.signature.ui.SignatureView
import studio.carbonylgroup.textfieldboxes.ExtendedEditText
import studio.carbonylgroup.textfieldboxes.TextFieldBoxes

object Binding {
    @JvmStatic
    @Suppress("UNUSED_PARAMETER")
    @BindingAdapter("bitmap")
    fun setDrawActions(view: SignatureView, bitmap: Bitmap?) {
        // Do nothing.
    }

    @JvmStatic
    @InverseBindingAdapter(attribute = "bitmap")
    fun getDrawActions(view: SignatureView): Bitmap {
        return view.createBitmap()
    }

    @JvmStatic
    @BindingAdapter(
            value = ["onBitmapChange", "bitmapAttrChanged"],
            requireAll = false)
    fun setDrawActionListener(
            view: SignatureView,
            onDrawActionsChangeListener: SignatureView.OnBitmapChangeListener?,
            inverseBindingListener: InverseBindingListener?) {
        val newListener = if (inverseBindingListener == null) {
            onDrawActionsChangeListener
        } else {
            object : SignatureView.OnBitmapChangeListener {
                override fun bitmapChanged(bitmap: Bitmap?) {
                    onDrawActionsChangeListener?.bitmapChanged(bitmap)
                    inverseBindingListener.onChange()
                }
            }
        }

        val oldListener = ListenerUtil.trackListener(view, newListener, R.id.drawActionsChangeListener)

        oldListener?.let { view.removeListener(it) }
        newListener?.let { view.addListener(it) }
    }

    @JvmStatic
    @BindingAdapter("srcCompat")
    fun setSrcCompat(imageView: AppCompatImageView, @DrawableRes drawable: Int?) {
        if (drawable != null && drawable != 0) {
            imageView.setImageResource(drawable)
        }
    }

    @JvmStatic
    @BindingAdapter("android:text")
    fun setText(view: TextView, @StringRes value: Int?) {
        if (value != null && value != 0) {
            view.text = view.context.getString(value)
        } else {
            view.text = ""
        }
    }

    @JvmStatic
    @BindingAdapter("progress")
    fun setProgress(view: CircleChartView, progress: Int?) {
        progress?.let { view.progress = it }
    }

    @JvmStatic
    @BindingAdapter("app:show")
    fun setShown(view: View, shown: Boolean?) {
        shown?.let {
            if (it) {
                view.visibility = View.VISIBLE
            } else {
                view.visibility = View.GONE
            }
        }
    }

    @JvmStatic
    @BindingAdapter("android:onTextChanged")
    fun setTextChangeListener(view: TextInputEditText, textWatcher: TextWatcher) {
        view.removeTextChangedListener(textWatcher)
        view.addTextChangedListener(textWatcher)
    }

    @JvmStatic
    @BindingAdapter("app:imageUrl")
    fun setImageUrl(view: ImageView, imageUrl: String?) {
        imageUrl?.let {
            Glide.with(view)
                    .load(it)
                    .apply(RequestOptions.centerCropTransform())
                    .apply(RequestOptions.placeholderOf(R.drawable.place_holder))
                    .into(view)
        }
    }

    @JvmStatic
    @BindingAdapter("app:onClickUrl")
    fun setOnClickUrl(view: Button, url: String?) {
        url?.let {
            view.setOnClickListener {
                view.context.startActivity(Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(url)
                })
            }
        }
    }

    @JvmStatic
    @BindingAdapter("android:onTextChanged")
    fun onTextChanged(view: ExtendedEditText, watcher: TextWatcher) {
        view.removeTextChangedListener(watcher)
        view.addTextChangedListener(watcher)
        if (view.text.isBlank()) {
            view.removeTextChangedListener(watcher)
        }
    }

    @JvmStatic
    @BindingAdapter("android:onTextChanged")
    fun onTextChanged(view: AppCompatEditText, watcher: TextWatcher) {
        view.removeTextChangedListener(watcher)
        if (view.text?.isBlank() ?: true) { return }
        view.addTextChangedListener(watcher)
    }

    @JvmStatic
    @BindingAdapter("app:errorText")
    fun setErrorText(view: TextFieldBoxes, errorText: String) {
        if (errorText.isNotEmpty()) view.setError(errorText, true)
    }

    @JvmStatic
    @BindingAdapter("app:errorTextAppearance")
    fun setErrorTextAppearance(view: TextView, errorText: String) {
        if (errorText.isNotEmpty()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                view.setTextAppearance(R.style.TextAppearance_ErrorText)
            else {
                @Suppress("DEPRECATION")
                view.setTextColor(view.context.resources.getColor(R.color.error_text_color))
            }
        }
    }

    @JvmStatic
    @BindingAdapter("app:htmlText")
    fun sethtmlText(view: TextView, text: String?) {
        text?.let {
            val formattedText = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT)
            } else {
                @Suppress("DEPRECATION")
                Html.fromHtml(text)
            }
            view.text = formattedText
        }
    }

    @JvmStatic
    @BindingAdapter("app:errorMsg")
    fun setErrorMessage(view: TextInputLayout, errorMessage: String) {
        if (errorMessage.isNotEmpty()) {
            view.error = errorMessage
        } else {
            view.isErrorEnabled = false
        }
    }

}
