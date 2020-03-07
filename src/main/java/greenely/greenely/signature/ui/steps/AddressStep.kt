package greenely.greenely.signature.ui.steps

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ScrollView
import android.widget.TextView
import com.stepstone.stepper.BlockingStep
import com.stepstone.stepper.StepperLayout
import com.stepstone.stepper.VerificationError
import dagger.android.support.AndroidSupportInjection
import greenely.greenely.R
import greenely.greenely.databinding.SignatureAddressStepBinding
import greenely.greenely.extensions.hideKeyboard
import greenely.greenely.signature.ui.SignatureListener
import greenely.greenely.signature.ui.SignatureViewModel
import greenely.greenely.signature.ui.models.Constants
import greenely.greenely.signature.ui.steps.confirmation_step.ConfirmationStep
import greenely.greenely.tracking.Tracker
import greenely.greenely.tracking.TrackerFactory
import greenely.greenely.utils.adjustScrollIfInputHasError
import greenely.greenely.utils.replaceFragmentWithHorizontalSlideAnimation
import greenely.greenely.utils.setCloseGreenIconAndNavigation
import greenely.greenely.utils.setRightBackGreenIconAndNavigation
import org.grunkspin.swedishformats.android.formatAsPostalCode
import javax.inject.Inject

class AddressStep : androidx.fragment.app.Fragment() {

    @Inject
    internal lateinit var tracker: Tracker

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: SignatureViewModel

    private lateinit var binding: SignatureAddressStepBinding

    private val allowedSpecialCharactersForAddress = "/,-"

    private var signatureListener: SignatureListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = SignatureAddressStepBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders
                .of(activity!!, viewModelFactory)[SignatureViewModel::class.java]

        binding.viewModel = viewModel

        binding.postNumberTextInput.formatAsPostalCode()

        binding.postRegionTextInput.filters = arrayOf(letterFilter)
        binding.streetAddressTextInput.filters = arrayOf(specialCharacterFilter)

        binding.toolbar.setRightBackGreenIconAndNavigation(activity)


        binding.postRegionTextInput.setOnEditorActionListener(
                TextView.OnEditorActionListener { view, actionId, event ->
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        signatureListener?.proceedToNextStep()
                        return@OnEditorActionListener true
                    }
                    false
                })

        binding.phoneNumberTextInput.addTextChangedListener(phoneNumberInputWatcher())



        adjustScrollIfInputHasError(binding.streetAddressTextInput,
                binding.streetAddressTextLayout,
                binding.scrollView)

        adjustScrollIfInputHasError(binding.postNumberTextInput,
                binding.postNumberTextLayout,
                binding.scrollView)

        adjustScrollIfInputHasError(binding.postRegionTextInput,
                binding.postRegionTextLayout,
                binding.scrollView)

        adjustScrollIfInputHasError(binding.phoneNumberTextInput,
                binding.phoneNumberTextInputLayout,
                binding.scrollView)

        binding.buttonNext.setOnClickListener { onNextClicked() }


    }


    fun hasValidationErrors(): Boolean {
        activity?.hideKeyboard()
        val validationErrors = viewModel.validateAddressStep()
        return validationErrors.hasAddressStepErrors()

    }

    fun onNextClicked() {
        if (!hasValidationErrors()) {
            trackAddressChangeEvent()
            fragmentManager?.replaceFragmentWithHorizontalSlideAnimation(ConfirmationStep())

        }

    }

    private fun trackAddressChangeEvent() {
        tracker.track(TrackerFactory().trackingEvent(getString(R.string.poa_checked_address), getString(R.string.poa_category)))
    }



    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
        this.signatureListener = activity as SignatureListener
    }

    private var letterFilter: InputFilter = InputFilter { source, start, end, dest, dstart, dend ->
        source.filter { Character.isWhitespace(it) || Character.isLetter(it) }.toString()
    }

    private var specialCharacterFilter: InputFilter = InputFilter { source, start, end, dest, dstart, dend ->
        source.filter {
            Character.isWhitespace(it) ||
                    (Character.isLetterOrDigit(it) || allowedSpecialCharactersForAddress.contains(it))
        }.toString()
    }

    private fun phoneNumberInputWatcher(): TextWatcher {
        return object : TextWatcher {


            override fun afterTextChanged(mEdit: Editable) {
                if (mEdit.length != Constants.PHONE_NUMBER_LENGTH) {
                    return
                }

                viewModel.validateAddressStep()

                binding.scrollView.fullScroll(ScrollView.FOCUS_DOWN)

                binding.phoneNumberTextInput.requestFocus()


            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        }
    }

    override fun onResume() {
        super.onResume()
        tracker.trackScreen("PoA Verify Address")

    }


}
