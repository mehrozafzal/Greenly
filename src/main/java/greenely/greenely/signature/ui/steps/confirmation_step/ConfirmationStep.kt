package greenely.greenely.signature.ui.steps.confirmation_step

import android.app.Dialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import com.stepstone.stepper.BlockingStep
import com.stepstone.stepper.StepperLayout
import com.stepstone.stepper.VerificationError
import dagger.android.support.AndroidSupportInjection
import greenely.greenely.R
import greenely.greenely.databinding.SignatureConfirmationStepBinding
import greenely.greenely.errors.exceptions.VerificationException
import greenely.greenely.extensions.dp
import greenely.greenely.extensions.hideKeyboard
import greenely.greenely.retail.models.RetailBankIdProcessJson
import greenely.greenely.retailonboarding.errors.RetailErrorHandler
import greenely.greenely.retailonboarding.models.BankIdState
import greenely.greenely.retailonboarding.models.CustomerConversionResponseJson
import greenely.greenely.signature.ui.SignatureViewModel
import greenely.greenely.signature.ui.errors.PoaErrorHandler
import greenely.greenely.signature.ui.models.ConfirmationStepListItemModel
import greenely.greenely.signature.ui.models.Constants.Companion.CONFIRMATION_SCREEN_RECYCLERVIEW_ITEM_MARGIN
import greenely.greenely.tracking.Tracker
import greenely.greenely.tracking.TrackerFactory
import greenely.greenely.utils.SpacingDecoration
import greenely.greenely.utils.pdfview.PdfRendererView
import greenely.greenely.utils.setCloseGreenIconAndNavigation
import greenely.greenely.utils.setRightBackGreenIconAndNavigation
import javax.inject.Inject

class ConfirmationStep : androidx.fragment.app.Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: SignatureViewModel

    private lateinit var binding: SignatureConfirmationStepBinding

    @Inject
    lateinit var tracker: Tracker


    lateinit var dialog: Dialog


    private val userInputList: ArrayList<ConfirmationStepListItemModel> = ArrayList()

    private lateinit var bankidOrderRef: String

    private var isProcessCompleted=false


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = SignatureConfirmationStepBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders
                .of(activity!!, viewModelFactory)[SignatureViewModel::class.java]

        binding.viewModel = viewModel

        tracker.trackScreen("PoA Summary")

        loadUserInput()

        binding.toolbar.setRightBackGreenIconAndNavigation(activity)


        binding.confirmationStepRecyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(activity)

        val dividerDecoration = SpacingDecoration(activity!!.dp(CONFIRMATION_SCREEN_RECYCLERVIEW_ITEM_MARGIN).toInt())
        binding.confirmationStepRecyclerView.addItemDecoration(dividerDecoration)

        // Access the RecyclerView Adapter and load the data into it
        binding.confirmationStepRecyclerView.adapter = ConfirmationScreenAdapter(userInputList, this.context!!)

        binding.btnSignWithBankId.setOnClickListener { validateAndProceed() }

        binding.powerOfAttorneyTerms.setOnClickListener {

            activity?.let {

                tracker.track(TrackerFactory().trackingEvent(
                        getString(R.string.poa_pdf_preview),
                        getString(R.string.poa_category),"POA Process"))

                startActivity(
                    Intent(it, PdfRendererView::class.java).apply {
                        putExtra(
                                "url",
                                getString(R.string.api_base) + getString(R.string.poa_path)
                        )
                    }
            )
            }
        }
    }

    private fun loadUserInput() {

        userInputList.clear()

        userInputList.add(ConfirmationStepListItemModel(
                getString(R.string.personal_number_label),
                viewModel.signatureInput.personalNumber.get())
        )

        userInputList.add(ConfirmationStepListItemModel(
                getString(R.string.postal_address_label),
                viewModel.signatureInput.address.get())
        )
        userInputList.add(ConfirmationStepListItemModel(
                getString(R.string.postal_code),
                viewModel.signatureInput.postalCode.get())
        )
        userInputList.add(ConfirmationStepListItemModel(
                getString(R.string.postal_region),
                viewModel.signatureInput.postalRegion.get())
        )

        userInputList.add(ConfirmationStepListItemModel(
                getString(R.string.phone_number),
                viewModel.signatureInput.phoneNumber.get())
        )

    }






    fun validateAndProceed() {

        if (binding.powerOfAttorneyToggle.isChecked) {

//                if (viewModel.isCombinedPoaProcess(retailOnboardConfig)) {
//                    tracker.track(TrackerFactory().trackingEvent(
//                            "retail_clicked_signup",
//                            "Retail",
//                            "Combined PoA"
//                    ))
//                } else {
//                    tracker.track(TrackerFactory().trackingEvent(
//                            "retail_clicked_signup",
//                            "Retail",
//                            "Retail PoA"
//                    ))
//                }


            viewModel.postCustomerInfo().observe(this,
                    Observer<CustomerConversionResponseJson?> { response ->

                        if (response?.bankidOrderRef!!.isNotEmpty()) {

                            bankidOrderRef = response.bankidOrderRef

                            trackPoaValuesConfirmed()
                            startPollingForBankIdProcessStatus(bankidOrderRef, false)

                            showBankIdPopup(bankidOrderRef)

                            openBankIdApp(response.bankidStartToken)

                        }

                    })

        } else
            viewModel.postVerificationError(getString(R.string.poa_read_and_accept_terms))

    }


    private fun trackPoaValuesConfirmed()
    {
        tracker.track(TrackerFactory().trackingEvent(
                            "poa_values_confirmed",
                            "POA"
                    ))
    }


    private fun showBankIdPopup(bankidOrderRef: String) {
        dialog = Dialog(context, R.style.AlertDialogTheme)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val view = layoutInflater.inflate(R.layout.bankid_popup_layout, null)
        dialog.setContentView(view)
        dialog.show()
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.setOnDismissListener {
            viewModel.cancelExistingRetailBankIDProcess(bankidOrderRef)
            viewModel.cancelDisposables()
            startPollingForBankIdProcessStatus(bankidOrderRef, true)
        }

        view.findViewById<Button>(R.id.canceldButton).setOnClickListener {
            dialog.dismiss()
        }
    }


    private fun openBankIdApp(bankIdStartToken: String?) {
        try {
            val intent = Intent()
            intent.setPackage("com.bankid.bus")
            intent.action = Intent.ACTION_VIEW
            intent.data = Uri.parse("bankid:///?autostarttoken=" + bankIdStartToken + "&redirect=null")
            startActivity(intent)
        } catch (e: Exception) {
            //do nothing if app not present
        }
    }


    private fun startPollingForBankIdProcessStatus(bankidOrderRef: String?, isCancelled: Boolean) {
        viewModel.getBankIdProcess(bankidOrderRef, System.currentTimeMillis(), isCancelled).observe(
                this,
                Observer<RetailBankIdProcessJson?> { bankidStatusResponse ->

                    if (bankidStatusResponse?.bankIdStatus.equals(BankIdState.COMPLETED)) {
                        dialog.hide()
                        isProcessCompleted=true
                        viewModel.setBankIdVerficationDone()

                    } else if (bankidStatusResponse?.bankIdStatus.equals(BankIdState.TIMEOUT)) {
                        dialog.hide()
                        viewModel.postVerificationError(getString(R.string.retail_action_interrupted))

                    } else if (!bankidStatusResponse?.bankIdStatus.equals(BankIdState.PENDING)) {
                        dialog.hide()
                    }
                }
        )
    }


    override fun onDestroy() {
        super.onDestroy()
        if(!isProcessCompleted)
            tracker.track(TrackerFactory().trackingEvent(
                    "poa_input_changed",
                    "POA"
            ))
    }


    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

}
