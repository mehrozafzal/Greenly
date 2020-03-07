package greenely.greenely.retailonboarding.ui

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import androidx.fragment.app.Fragment
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import dagger.android.support.AndroidSupportInjection
import greenely.greenely.R
import greenely.greenely.databinding.RetailOnboardingAddressStepBinding
import greenely.greenely.extensions.hideKeyboard
import greenely.greenely.retail.models.RetailOnboardConfig
import greenely.greenely.retailonboarding.models.CustomerInfoErrorModel
import greenely.greenely.retailonboarding.steps.ContactInformationStepFragment
import greenely.greenely.retailonboarding.ui.util.CombinedPOAFlow
import greenely.greenely.signature.ui.models.Constants
import greenely.greenely.tracking.Tracker
import greenely.greenely.tracking.TrackerFactory
import org.grunkspin.swedishformats.android.formatAsLongPersonalNumber
import org.grunkspin.swedishformats.android.formatAsPostalCode
import org.grunkspin.swedishformats.unformatPersonalNumber
import javax.inject.Inject

class CombinedPOAAdressStepFragment : androidx.fragment.app.Fragment() {

    @Inject
    internal lateinit var factory: ViewModelProvider.Factory
    internal lateinit var binding: RetailOnboardingAddressStepBinding
    private lateinit var viewModel: RetailOnboardingViewModel
    @Inject
    lateinit var tracker: Tracker
    private val allowedSpecialCharactersForAddress = "/,-"

    private val config by lazy {
        arguments?.getSerializable(RetailOnboardConfig.KEY_CONFIG) as RetailOnboardConfig
    }

    private val retailOnboardConfig: RetailOnboardConfig by lazy {
        arguments?.getSerializable(RetailOnboardConfig.KEY_CONFIG) as RetailOnboardConfig
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = RetailOnboardingAddressStepBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(activity!!, factory)[RetailOnboardingViewModel::class.java]

        binding.viewModel = viewModel

        binding.personalNumberTextInput.formatAsLongPersonalNumber()
        binding.postNumber.formatAsPostalCode()


        binding.toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }

        binding.buttonNext.setOnClickListener {
            validateAndProceed()
        }

        binding.city.filters = arrayOf(letterFilter)
        binding.streetAddress.filters = arrayOf(specialCharacterFilter)

        addOnFocusChangeListener(binding.personalNumberTextInput, binding.personalNumberTextInputLayout)
        addOnFocusChangeListener(binding.city, binding.cityTextInputLayout)
        addOnFocusChangeListener(binding.streetAddress, binding.streetAddressTextInputLayout)
        addOnFocusChangeListener(binding.postNumber, binding.postNumberTextInputLayout)

        binding.city.setOnEditorActionListener(TextView.OnEditorActionListener { view, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                validateAndProceed()
                return@OnEditorActionListener true
            }
            false
        })

        binding.constraintLayout.onFocusChangeListener = View.OnFocusChangeListener { view, hasFocus ->
            activity?.hideKeyboard()
        }

        binding.personalNumberTextInput.requestFocus()

        handleHiddenAddressFields()

        setPersonalNumber()
        setTitleAndSubtitle()
    }

    private fun setPersonalNumber() {
        config.flow?.let {
            when (it) {
                is CombinedPOAFlow -> {
                    viewModel.customerInfoInput.personalNumber.set(it.personalNumber)
                    viewModel.customerInfoInput.isFromPoaProcess.set(true)
//                    binding.personalNumberTextInput.visibility=View.GONE

                }

            }
        }
    }

    private fun prefillAddressScreenData() {
        viewModel.preFillFromPersonalNumber(
                viewModel.customerInfoInput.personalNumber.get().unformatPersonalNumber()
        ).observe(this, Observer {})
    }

    private fun addOnFocusChangeListener(inputBox: TextInputEditText, inputBoxLayout: TextInputLayout) {
        inputBox.onFocusChangeListener = View.OnFocusChangeListener { view, hasFocus ->
            if (hasFocus && !isViewFullyVisible(inputBoxLayout)) {
                binding.scrollview.post {
                    binding.scrollview.scrollTo(0, inputBoxLayout.top - 40)
                }
            }
        }
    }

    private fun validateAndProceed() {

        activity?.hideKeyboard()

        val validation = viewModel.validateAddressStep()

        if (!validation.hasAddressStepErrors()) {

            activity?.hideKeyboard(view)

            tracker.track(TrackerFactory().trackingEvent(
                    "retail_checked_address",
                    "Retail",
                    "Combined PoA"
            ))

            var fragment = ContactInformationStepFragment()
            fragment.arguments = Bundle()
            fragment.arguments?.putSerializable(RetailOnboardConfig.KEY_CONFIG, retailOnboardConfig)

            fragmentManager
                    ?.beginTransaction()
                    ?.addToBackStack(this.javaClass.simpleName)
                    ?.setCustomAnimations(
                            R.anim.enter_from_right,
                            R.anim.exit_to_left,
                            R.anim.enter_from_left,
                            R.anim.exit_to_right)
                    ?.replace(R.id.content, fragment)
                    ?.commit()
        } else {
            focusOnFirstError(validation)
        }
    }

    private fun focusOnFirstError(validation: CustomerInfoErrorModel) {
        when {
            validation.hasPersonalNumberError() -> {
                binding.personalNumberTextInput.requestFocus()
            }
            validation.hasAddressError() -> {
                binding.streetAddress.requestFocus()

            }
            validation.hasPostalCodeError() -> {
                binding.postNumber.requestFocus()

            }
            validation.hasPostalRegionError() -> {
                binding.city.requestFocus()
            }
        }
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onResume() {
        super.onResume()
        binding.personalNumberTextInput.addTextChangedListener(personalNumberTextWatcher())
        tracker.trackScreen("Retail Verify Address")
    }

    private fun isViewFullyVisible(view: View): Boolean {
        val scrollBounds = Rect()
        binding.scrollview.getDrawingRect(scrollBounds)

        val top = view.y
        val bottom = top + view.height

        return scrollBounds.top < top && scrollBounds.bottom > bottom
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

    private fun personalNumberTextWatcher(): TextWatcher {
        return object : TextWatcher {

            var handler = Handler(Looper.getMainLooper())
            var workRunnable: Runnable = Runnable { }

            override fun afterTextChanged(mEdit: Editable) {
                if (mEdit.length != Constants.PERSONAL_NUMBER_LENGTH ||
                        !viewModel.isInputtedPersonalNumberValid()) {
                    return
                }

                handler.removeCallbacks(workRunnable)

                workRunnable = Runnable {
                    prefillAddressScreenData()
                    handleHiddenAddressFields()
                    activity?.hideKeyboard()
                }

                //afterTextChanged event fires multiple times after user finishes typing
                //Make GET request only when 50ms passes from last afterTextChanged event
                handler.postDelayed(workRunnable, 50)
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        }
    }

    private fun setTitleAndSubtitle() {
      binding.textView39.setText(R.string.residence_address_label)
        binding.textView41.setText(R.string.fill_details_label)

    }

    private fun handleHiddenAddressFields() {
        return if (!viewModel.isInputtedPersonalNumberValid()) {
        } else {
            binding.streetAddressTextInputLayout.visibility = View.VISIBLE
            binding.postNumberTextInputLayout.visibility = View.VISIBLE
            binding.cityTextInputLayout.visibility = View.VISIBLE
        }
    }
}