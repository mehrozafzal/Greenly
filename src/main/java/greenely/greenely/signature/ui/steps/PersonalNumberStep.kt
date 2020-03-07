package greenely.greenely.signature.ui.steps

import android.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import android.content.Context
import androidx.databinding.DataBindingUtil
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.CompoundButton
import android.widget.ScrollView
import android.widget.TextView
import com.stepstone.stepper.BlockingStep
import com.stepstone.stepper.StepperLayout
import com.stepstone.stepper.VerificationError
import dagger.android.support.AndroidSupportInjection
import greenely.greenely.R
import greenely.greenely.databinding.RetailTcDialogLayoutBinding
import greenely.greenely.databinding.SignaturePersonalNumberStepBinding
import greenely.greenely.extensions.hideKeyboard
import greenely.greenely.retailonboarding.ui.RetailOnboardingActivity
import greenely.greenely.signature.ui.SignatureActivity
import greenely.greenely.signature.ui.SignatureListener
import greenely.greenely.signature.ui.SignatureViewModel
import greenely.greenely.signature.ui.models.Constants
import greenely.greenely.signature.ui.models.PreFillInfo
import greenely.greenely.tracking.Tracker
import greenely.greenely.tracking.TrackerFactory
import greenely.greenely.utils.adjustScrollIfInputHasError
import greenely.greenely.utils.replaceFragmentWithHorizontalSlideAnimation
import greenely.greenely.utils.setCloseGreenIconAndNavigation
import greenely.greenely.utils.underlineText
import org.grunkspin.swedishformats.android.formatAsLongPersonalNumber
import org.grunkspin.swedishformats.android.formatAsMeterId
import javax.inject.Inject

class PersonalNumberStep : androidx.fragment.app.Fragment() {

    @Inject
    internal lateinit var tracker: Tracker

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: SignatureViewModel
    private lateinit var binding: SignaturePersonalNumberStepBinding

    private var signatureListener: SignatureListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = SignaturePersonalNumberStepBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders
                .of(activity!!, viewModelFactory)[SignatureViewModel::class.java]
        viewModel.fetchRetailState()

        binding.viewModel = viewModel

        binding.personalNumberTextInput.formatAsLongPersonalNumber()

        binding.toolbar.setCloseGreenIconAndNavigation(activity)


        binding.takeCare.setOnClickListener(retailClickListener)

        binding.label1.setOnClickListener(retailClickListener)

        binding.label2.setOnClickListener(retailClickListener)

        binding.label3.setOnClickListener(retailClickListener)

        binding.label3.underlineText()

        binding.buttonNext.setOnClickListener { onNextClick() }

        binding.toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }

        adjustScrollIfInputHasError(binding.personalNumberTextInput,
                binding.personalNumberTextInputLayout,
                binding.scrollView)



        viewModel.checkChangeListener = CompoundButton.OnCheckedChangeListener { compoundButton: CompoundButton, b: Boolean ->
            tracker.track(TrackerFactory().trackingEvent("poa_clicked_add_retail", getString(R.string.poa_category)))
            viewModel.onRetailToggleClicked(b)
        }



    }



    fun onNextClick() {
        checkIfRetailOnboardRequiredAndNavigate()

    }

    private fun checkIfRetailOnboardRequiredAndNavigate() {
        if (verifyStep()) {
            trackPoaIdentity()
            if (viewModel.signatureInput.signUpForRetail.get()) {
                navigateToRetailOnboardScreen()
            } else {
                fragmentManager?.replaceFragmentWithHorizontalSlideAnimation(AddressStep())
            }
        }
    }

    private fun navigateToRetailOnboardScreen() {
        context?.let {
            activity?.startActivityForResult(RetailOnboardingActivity.initiateCombinedPOAFlow(it, viewModel.signatureInput.personalNumber.get()), SignatureActivity.RETAIL_ONBOARD_REQUEST_CODE)
            activity?.overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);

        }
    }

    private fun prefillFieldsFromPersonalNumber() {
        viewModel.preFillFromPersonalNumber().observe(this, Observer<PreFillInfo?> {
            it.let {
                val firstName = viewModel.signatureInput.firstName.get()
                if (firstName.isEmpty() || firstName.isBlank()) {
                    return@Observer
                }
                activity?.hideKeyboard()
            }
        })
    }

    fun verifyStep(): Boolean {
        activity?.hideKeyboard()
        val validationErrors = viewModel.validatePersonalNumberStep()
        return if (validationErrors.hasPersonalNumberStepErrors()) {
            return false
        } else true
    }


    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
        this.signatureListener = activity as SignatureListener
    }


    override fun onResume() {
        super.onResume()
        tracker.trackScreen("PoA PNO and Meter ID")
        binding.personalNumberTextInput.addTextChangedListener(personalNumberTextWatcher())
    }

    private fun trackPoaIdentity() {
        if (!viewModel.poaType.isCombinedPOA()) {
            tracker.track(TrackerFactory().trackingEvent(getString(R.string.poa_identified), getString(R.string.poa_category), "PoA Process"))
        } else
            tracker.track(TrackerFactory().trackingEvent(getString(R.string.poa_identified), getString(R.string.poa_category), "Combined Process"))
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
                workRunnable = Runnable { prefillFieldsFromPersonalNumber() }

                //afterTextChanged event fires multiple times after user finishes typing
                //Make GET request only when 50ms passes from last afterTextChanged event
                handler.postDelayed(workRunnable, 50)
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        }
    }

    private val retailClickListener: View.OnClickListener = View.OnClickListener { view ->
        displayRetailInfoDialog()
    }


    private fun displayRetailInfoDialog() {
        context?.let {
            var builder = AlertDialog.Builder(it, R.style.AlertDialogTheme2)
            val binding = DataBindingUtil.inflate<RetailTcDialogLayoutBinding>(LayoutInflater.from(context), R.layout.retail_tc_dialog_layout, null, false)
            builder.setView(binding.root)
            var dialog = builder.create()
            dialog.show()
            dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            binding.close.setOnClickListener {
                dialog.dismiss()
            }

            dialog.window.setGravity(Gravity.CENTER);


            trackPOARetailDialogOpen()

            dialog.show();


        }
    }

    private fun trackPOARetailDialogOpen() {
        tracker.track(TrackerFactory().trackingEvent("poa_opened_retail_dialog", getString(R.string.poa_category)))
    }


}

