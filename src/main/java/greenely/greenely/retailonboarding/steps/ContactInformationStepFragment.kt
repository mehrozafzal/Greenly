package greenely.greenely.retailonboarding.steps

import android.app.Activity
import android.app.Dialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import androidx.fragment.app.Fragment
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import com.stepstone.stepper.VerificationError
import dagger.android.support.AndroidSupportInjection
import greenely.greenely.R
import greenely.greenely.databinding.RetailOnboardingContactInfoBinding
import greenely.greenely.errors.exceptions.VerificationException
import greenely.greenely.extensions.hideKeyboard
import greenely.greenely.retail.models.RetailBankIdProcessJson
import greenely.greenely.retail.models.RetailOnboardConfig
import greenely.greenely.retailonboarding.broadcast.NetworkState
import greenely.greenely.retailonboarding.broadcast.NetworkUtil
import greenely.greenely.retailonboarding.errors.RetailErrorHandler
import greenely.greenely.retailonboarding.models.BankIdState
import greenely.greenely.retailonboarding.models.CustomerConversionResponseJson
import greenely.greenely.retailonboarding.ui.OnboardCompletedFragment
import greenely.greenely.retailonboarding.ui.RetailOnboardingActivity
import greenely.greenely.retailonboarding.ui.RetailOnboardingViewModel
import greenely.greenely.retailonboarding.ui.util.CombinedPOAFlow
import greenely.greenely.signature.ui.SignatureActivity
import greenely.greenely.tracking.Tracker
import greenely.greenely.tracking.TrackerFactory
import greenely.greenely.utils.pdfview.PdfRendererView
import javax.inject.Inject

class ContactInformationStepFragment : androidx.fragment.app.Fragment() {

    @Inject
    internal lateinit var factory: ViewModelProvider.Factory

    @Inject
    lateinit var errorHandler: RetailErrorHandler

    @Inject
    lateinit var tracker: Tracker

    internal lateinit var binding: RetailOnboardingContactInfoBinding

    private lateinit var viewModel: RetailOnboardingViewModel

    private lateinit var bankidOrderRef: String

    private var networkWasDisconnected: Boolean = false

    lateinit var dialog: Dialog

    private val focusedBoxTopMargin: Int = 40

    private val retailOnboardConfig: RetailOnboardConfig by lazy {
        arguments?.getSerializable(RetailOnboardConfig.KEY_CONFIG) as RetailOnboardConfig
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = RetailOnboardingContactInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            fragmentManager?.popBackStack()
        }

        viewModel = ViewModelProviders.of(activity!!, factory)[RetailOnboardingViewModel::class.java]

        binding.viewModel = viewModel

        viewModel.errors.observe(this, Observer { it?.let { errorHandler.handleError(it) } })

        binding.buttonNext.setOnClickListener {
            validateAndProceed()
        }

        binding.phoneNumberTextInput.setOnEditorActionListener(
                TextView.OnEditorActionListener { view, actionId, event ->
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        validateAndProceed()
                        return@OnEditorActionListener true
                    }
                    false
                })

        binding.terms.setOnClickListener {

            tracker.track(TrackerFactory().trackingEvent(
                    "retail_opened_terms",
                    "Retail",
                    "Retail Contact Form"
            ))

            tracker.trackScreen("Retail Contract Terms")


            openPDF("https://api2.greenely.com/static/avtalsvillkor_elhandel.pdf")
        }

        binding.powerOfAttorneyTerms.setOnClickListener {


            if (viewModel.isCombinedPoaProcess(retailOnboardConfig)) {
                tracker.track(TrackerFactory().trackingEvent(
                        "retail_opened_poa",
                        "Retail",
                        "Combined PoA"
                ))
            } else {
                tracker.track(TrackerFactory().trackingEvent(
                        "retail_opened_poa",
                        "Retail",
                        "Retail PoA"
                ))
            }


            tracker.trackScreen("Retail Contract Terms")


            openPDF(viewModel.getPoaUrl(retailOnboardConfig))
        }

        binding.emailTextInput.filters = arrayOf(whiteSpaceFilter)

        binding.contactInfoLayout.onFocusChangeListener = View.OnFocusChangeListener { view, hasFocus ->
            activity?.hideKeyboard()
        }

        addOnFocusChangeListener(binding.emailTextInput, binding.emailAddressTextInputLayout)
        addOnFocusChangeListener(binding.phoneNumberTextInput, binding.phoneNumberTextInputLayout)
    }

    fun validateAndProceed() {

        activity?.hideKeyboard()

        val validation = viewModel.validateContactInformation()

        if (!validation.hasContactInfoErrors()) {

            if (binding.userTermsToggle.isChecked && binding.powerOfAttorneyToggle.isChecked) {

                if (viewModel.isCombinedPoaProcess(retailOnboardConfig)) {
                    tracker.track(TrackerFactory().trackingEvent(
                            "retail_clicked_signup",
                            "Retail",
                            "Combined PoA"
                    ))
                } else {
                    tracker.track(TrackerFactory().trackingEvent(
                            "retail_clicked_signup",
                            "Retail",
                            "Retail PoA"
                    ))
                }

                var promocode = retailOnboardConfig.flow?.promoCode

                viewModel.postCustomerInfo(promocode).observe(this,
                        Observer<CustomerConversionResponseJson?> { response ->

                            if (response?.bankidOrderRef!!.isNotEmpty()) {

                                bankidOrderRef = response.bankidOrderRef

                                startPollingForBankIdProcessStatus(bankidOrderRef, false)

                                showBankIdPopup(bankidOrderRef)

                                openBankIdApp(response.bankidStartToken)

                            }

                        })

            } else errorHandler.handleError(
                    VerificationException(
                            VerificationError(getString(R.string.retail_read_and_accept_terms))
                    )
            )
        }
    }

    private fun openPDF(url: String) {
        activity?.startActivity(Intent(context, PdfRendererView::class.java).apply { putExtra("url", url) })
    }

    private fun openBankIdApp(bankIdStartToken: String?) {
        val intent = Intent()
        intent.setPackage("com.bankid.bus")
        intent.action = Intent.ACTION_VIEW
        intent.data = Uri.parse("bankid:///?autostarttoken=" + bankIdStartToken + "&redirect=null")
        startActivity(intent)
    }

    private fun startPollingForBankIdProcessStatus(bankidOrderRef: String?, isCancelled: Boolean) {
        viewModel.getRetailBankIdProcess(bankidOrderRef, System.currentTimeMillis(), isCancelled).observe(
                this,
                Observer<RetailBankIdProcessJson?> { bankidStatusResponse ->

                    if (bankidStatusResponse?.bankIdStatus.equals(BankIdState.COMPLETED)) {
                        navigateToNextScreen()
                        dialog.hide()

                    } else if (bankidStatusResponse?.bankIdStatus.equals(BankIdState.TIMEOUT)) {
                        errorHandler.handleError(
                                VerificationException(
                                        VerificationError(getString(R.string.retail_action_interrupted))
                                )
                        )
                        dialog.hide()
                    } else if (!bankidStatusResponse?.bankIdStatus.equals(BankIdState.PENDING)) {
                        dialog.hide()
                    }
                }
        )
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
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

    private fun addOnFocusChangeListener(inputBox: TextInputEditText, inputBoxLayout: TextInputLayout) {
        inputBox.onFocusChangeListener = View.OnFocusChangeListener { view, hasFocus ->
            if (hasFocus && !isViewFullyVisible(inputBoxLayout)) {
                binding.scrollview.post {
                    binding.scrollview.scrollTo(0, inputBoxLayout.top - focusedBoxTopMargin)
                }
            }
        }
    }

    private fun isViewFullyVisible(view: View): Boolean {
        val scrollBounds = Rect()
        binding.scrollview.getDrawingRect(scrollBounds)

        val top = view.y
        val bottom = top + view.height

        return scrollBounds.top < top && scrollBounds.bottom > bottom
    }

    private val networkStatusChangeListener = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val networkState = NetworkUtil.getConnectivityStatus(context)

            if (networkState == NetworkState.DISCONNECTED) {
                viewModel.cancelDisposables()
                networkWasDisconnected = true
            } else if (networkState == NetworkState.CONNECTED && networkWasDisconnected) {
                networkWasDisconnected = false
                startPollingForBankIdProcessStatus(bankidOrderRef, false)
                if (!dialog.isShowing) {
                    showBankIdPopup(bankidOrderRef)
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        context?.unregisterReceiver(networkStatusChangeListener)
    }

    override fun onResume() {
        super.onResume()
        tracker.trackScreen("Retail Contact Form")

        val intentFilter = IntentFilter()
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        context?.registerReceiver(networkStatusChangeListener, intentFilter)
    }

    private var whiteSpaceFilter: InputFilter = InputFilter { source, start, end, dest, dstart, dend ->
        source.toString().filterNot { it.isWhitespace() }
    }

    private fun navigateToNextScreen() {


        when (retailOnboardConfig.flow) {
            is CombinedPOAFlow -> {
                var fragment = OnboardCompletedFragment()
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


            }
            else -> {
                activity?.setResult(Activity.RESULT_OK)
                activity?.finish()
            }

        }

    }
}
