package greenely.greenely.solaranalysis.ui.householdinfo.steps

import android.app.Dialog
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import androidx.annotation.VisibleForTesting
import androidx.fragment.app.Fragment
import android.telephony.PhoneNumberFormattingTextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.google.android.material.snackbar.Snackbar
import com.stepstone.stepper.BlockingStep
import com.stepstone.stepper.StepperLayout
import com.stepstone.stepper.VerificationError
import dagger.android.support.AndroidSupportInjection
import greenely.greenely.R
import greenely.greenely.competefriend.json.rankListJson.FriendsItemJson
import greenely.greenely.databinding.DialogDeleteFriendBinding
import greenely.greenely.databinding.DialogSendSolarAnalysisBinding
import greenely.greenely.databinding.SolarAnalysisContactInfoActivityBinding
import greenely.greenely.errors.HasSnackbarView
import greenely.greenely.extensions.hideKeyboard
import greenely.greenely.solaranalysis.ui.householdinfo.SolarAnalysisErrorHandler
import greenely.greenely.solaranalysis.ui.householdinfo.SolarAnalysisViewModel
import greenely.greenely.solaranalysis.ui.householdinfo.charting.ChartManager
import greenely.greenely.tracking.Tracker
import greenely.greenely.tracking.TrackerFactory
import javax.inject.Inject

class ContactInfoStep : androidx.fragment.app.Fragment(), BlockingStep, HasSnackbarView {
    @VisibleForTesting
    lateinit var binding: SolarAnalysisContactInfoActivityBinding

    override val snackbarView: View
        get() = binding.coordinatorLayout

    @Inject
    internal lateinit var factory: ViewModelProvider.Factory
    private lateinit var viewModel: SolarAnalysisViewModel

    @Inject
    internal lateinit var tracker: Tracker

    @Inject
    internal lateinit var chartManager: ChartManager

    @Inject
    internal lateinit var errorHandler: SolarAnalysisErrorHandler

    private var isOptionSelected: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = SolarAnalysisContactInfoActivityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!, factory)[SolarAnalysisViewModel::class.java]
        binding.viewModel = viewModel
        bindViews()
    }

    override fun onSelected() {
        tracker.trackScreen("Solar Analysis Contact Form")
        setUpPhoneNumberFormatter()
    }

    override fun verifyStep(): VerificationError? {
        activity!!.hideKeyboard()
        viewModel.validateContactInfo()
        return if (viewModel.errorModel.hasErrors()) {
            VerificationError("") // We don't care about the message
        } else null
    }

    override fun onError(error: VerificationError) {

    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    private fun setUpPhoneNumberFormatter() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            binding.phoneNumberTextInput.addTextChangedListener(PhoneNumberFormattingTextWatcher("SE"))
        } else {
            binding.phoneNumberTextInput.addTextChangedListener(PhoneNumberFormattingTextWatcher())
        }
    }

    override fun onBackClicked(callback: StepperLayout.OnBackClickedCallback) {
        activity!!.hideKeyboard()
        callback.goToPrevStep()
    }

    override fun onCompleteClicked(callback: StepperLayout.OnCompleteClickedCallback) {
        if (isOptionSelected) {
            showDialog()
        } else {
          /*  Snackbar.make(snackbarView, "Vänligen välj ett alternativ", Snackbar.LENGTH_LONG)
                    .show()*/
        }
    }

    override fun onNextClicked(callback: StepperLayout.OnNextClickedCallback) {
    }

    private fun bindViews() {
        binding.solarAnalysisRadioGrp?.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.solarAnalysis_yesRadioBtn) {
                binding.solarAnalysisSendBtn?.isEnabled = true
                isOptionSelected = true
            } else {
                binding.solarAnalysisSendBtn?.isEnabled = true
                isOptionSelected = true
            }
        }
    }


    private fun showDialog() {
        val dialog = Dialog(context!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(true)
        val binding: DialogSendSolarAnalysisBinding = DialogSendSolarAnalysisBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(binding.root)
        binding.dialogSolarAnalysisYes.setOnClickListener {
            activity!!.hideKeyboard()
            viewModel.contactInfo.qualityLead.set(true)
            tracker.track(TrackerFactory().trackingEvent("sa_interest_sent", getString(R.string.solar_analysis_tracking_category)))
            viewModel.requestContact()
            dialog.dismiss()
        }
        binding.dialogSolarAnalysisNo.setOnClickListener {
            viewModel.contactInfo.qualityLead.set(false)
            dialog.dismiss()
            activity!!.hideKeyboard()
            activity?.finish()
        }

        dialog.show()
    }
}
