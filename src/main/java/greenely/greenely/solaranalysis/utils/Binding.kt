package greenely.greenely.solaranalysis.utils

import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.databinding.adapters.ListenerUtil
import android.view.View
import com.stepstone.stepper.StepperLayout
import com.stepstone.stepper.VerificationError
import greenely.greenely.R

object StepperAdapters {

    @JvmStatic
    @BindingAdapter("selectedStep")
    fun setSelectedStep(view: StepperLayout, selectedStep: Int) {
        if (selectedStep != view.currentStepPosition) {
            view.currentStepPosition = selectedStep
        }
    }

    @JvmStatic
    @InverseBindingAdapter(attribute = "selectedStep")
    fun getSelectedStep(view: StepperLayout): Int = view.currentStepPosition

    @JvmStatic
    @BindingAdapter(
            value = ["stepperListener", "selectedStepAttrChanged"],
            requireAll = false
    )
    fun setListeners(
            view: StepperLayout,
            listener: StepperLayout.StepperListener?,
            inverseBindingListener: InverseBindingListener?
    ) {
        val newListener = if (inverseBindingListener == null) {
            listener
        } else {
            object : StepperLayout.StepperListener {
                override fun onStepSelected(newStepPosition: Int) {
                    listener?.onStepSelected(newStepPosition)
                    inverseBindingListener.onChange()
                }

                override fun onError(verificationError: VerificationError?) {
                    listener?.onError(verificationError)
                }

                override fun onReturn() {
                    listener?.onReturn()
                }

                override fun onCompleted(completeButton: View?) {
                    listener?.onCompleted(completeButton)
                }
            }
        }

        val oldListener = ListenerUtil.trackListener(view, newListener, R.id.selectedStepListener)

        oldListener?.let { view.setListener(StepperLayout.StepperListener.NULL) }
        newListener?.let { view.setListener(it) }
    }
}

