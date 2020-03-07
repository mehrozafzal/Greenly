package greenely.greenely.views

import android.content.Context
import androidx.databinding.DataBindingUtil
import androidx.annotation.MainThread
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import greenely.greenely.R
import greenely.greenely.databinding.ConsumptionForTimeRangeHeaderBinding

class ConsumptionForTimeRangeHeader @JvmOverloads constructor(
        context: Context,
        attributeSet: AttributeSet? = null,
        defaultStyleAttr: Int = 0
) : FrameLayout(context, attributeSet, defaultStyleAttr) {

    private val binding: ConsumptionForTimeRangeHeaderBinding

    var model: Model?
        get() = binding.model
        set(value) {
            binding.model = value
        }

    init {
        val layoutInflater = LayoutInflater.from(context)
        binding = ConsumptionForTimeRangeHeaderBinding.inflate(layoutInflater, this, true)
    }

    interface Model {
        val timeRangeUnit: String
        val timeRange: String
        val consumption: String
    }
}

