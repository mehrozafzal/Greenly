package greenely.greenely.setuphousehold.ui

import android.app.Activity
import android.app.ProgressDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import android.content.Intent
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import androidx.annotation.VisibleForTesting
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import greenely.greenely.R
import greenely.greenely.accountsetup.AccountSetupNextHandler
import greenely.greenely.accountsetup.MainAccountSetupNextHandler
import greenely.greenely.accountsetup.models.AccountSetupNext
import greenely.greenely.databinding.SetupHouseholdActivityBinding
import greenely.greenely.setuphousehold.models.json.HouseholdRequestJsonModel
import greenely.greenely.setuphousehold.ui.events.EventHandler
import greenely.greenely.setuphousehold.ui.events.EventHandlerFactory
import greenely.greenely.setuphousehold.ui.model.HouseholdStep
import greenely.greenely.tracking.Tracker
import greenely.greenely.tracking.TrackerFactory
import javax.inject.Inject

class SetupHouseholdActivity @Inject constructor() : AppCompatActivity(), HasSupportFragmentInjector {

    companion object {
        const val FACILITY_TYPE_APARTMENT: Int = 1
        const val COMPLETE_REONBOARDING_ACTIVITY_CODE: Int = 3
        const val SHOULD_REONBOARD: String = "shouldReOnboard"
        const val REONBOARDING_COMPLETED: String = "reOnboardingCompleted"
    }

    private lateinit var viewModel: SetupHouseholdViewModel

    @Inject
    internal lateinit var eventHandlerFactory: EventHandlerFactory

    @Inject
    internal lateinit var dispatchingFragmentInjector: DispatchingAndroidInjector<androidx.fragment.app.Fragment>

    @Inject
    lateinit var tracker: Tracker

    @Inject
    internal lateinit var factory: ViewModelProvider.Factory

    @Inject
    internal lateinit var errorHandler: SetupHouseholdErrorHandler

    @VisibleForTesting
    internal lateinit var eventHandler: EventHandler

    @VisibleForTesting
    internal lateinit var binding: SetupHouseholdActivityBinding

    private var reOnboardingEnabled: Boolean = false

    private val navigationHandler: AccountSetupNextHandler by lazy {
        MainAccountSetupNextHandler(this)
    }

    private val loadingDialog by lazy {
        ProgressDialog(this).apply {
            setMessage(getString(R.string.loading_profile))
            setCanceledOnTouchOutside(false)
            setCancelable(false)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this, factory)[SetupHouseholdViewModel::class.java]

        binding = DataBindingUtil.setContentView(this, R.layout.setup_household_activity)

        reOnboardingEnabled = intent.getBooleanExtra(SHOULD_REONBOARD, false)

        viewModel.shouldReOnboard = reOnboardingEnabled

        eventHandler = eventHandlerFactory.createEventHandler(binding)

        viewModel.householdConfigurationOptions.observe(this, Observer {
            it?.let {
                binding.stepper.adapter = SetupHouseholdStepAdapter(this, supportFragmentManager)

                if (reOnboardingEnabled) {
                    //skip intro
                    binding.stepper.currentStepPosition = HouseholdStep.MUNICIPALITY.ordinal
                }
            }
        })

        viewModel.events.observe(this, Observer {
            it?.let { eventHandler.handleEvent(it) }
        })

        viewModel.errors.observe(this, Observer {
            it?.let { errorHandler.handleError(it) }
        })

        viewModel.disruptiveLoading.observe(this, Observer {
            it?.let { showLoading(it) }
        })

        if (viewModel.shouldReOnboard) {
            viewModel.updateHouseholdResponse.observe(this, Observer {
                it?.let {
                    showLoading(false)
                    navigationHandler.navigateTo(AccountSetupNext.REONBOARDING_COMPLETED)
                }
            })
            prefillHouseholdParameters()
        } else {
            viewModel.householdResponse.observe(this, Observer {
                it?.let {
                    showLoading(false)
                    trackDoneEvent()
                    tracker.identify(it.userIdentificationUpdate)
                    navigationHandler.navigateTo(AccountSetupNext.NO_NEXT_STEP)
                }
            })
        }
    }

    private fun prefillHouseholdParameters() {
        viewModel.householdParameters.observe(this, Observer<HouseholdRequestJsonModel?> {

            //prefill shared values between apartment and house
            viewModel.householdRequest.facilityTypeId.set(it?.facilityTypeId)
            viewModel.householdRequest.municipalityId.set(it?.municipalityId)
            viewModel.householdRequest.facilityAreaId.set(it?.facilityAreaId)
            viewModel.householdRequest.occupants.set(it?.occupants)

            //prefill house values
            if (viewModel.householdRequest.facilityTypeId.get() != FACILITY_TYPE_APARTMENT) {
                viewModel.householdRequest.heatingTypeId.set(it?.heatingTypeId)
                viewModel.householdRequest.secondaryHeatingTypeId.set(it?.secondaryHeatingTypeId)
                viewModel.householdRequest.tertiaryHeatingTypeId.set(it?.tertiaryHeatingTypeId)
                viewModel.householdRequest.quaternaryHeatingTypeId.set(it?.quaternaryHeatingTypeId)
                viewModel.householdRequest.constructionYearId.set(it?.constructionYearId)
                viewModel.householdRequest.electricCarCountId.set(it?.electricCarCountId)
            }
            //if it's apartment then backend takes care of nulling house related parameters
            //we just set heating type as 1
            else {
                viewModel.householdRequest.heatingTypeId.set(FACILITY_TYPE_APARTMENT)
            }
        })
    }

    override fun supportFragmentInjector(): AndroidInjector<androidx.fragment.app.Fragment> = dispatchingFragmentInjector

    override fun onPause() {
        super.onPause()
        loadingDialog.dismiss()
    }

    private fun trackDoneEvent() {
        tracker.track(TrackerFactory().trackingEvent("onboarding_done", ""))
    }

    private fun showLoading(showLoading: Boolean) {
        if (showLoading) loadingDialog.show()
        else loadingDialog.hide()
    }

    override fun onBackPressed() {
        binding.stepper.onBackClicked()
    }

    override fun finish() {
        if (viewModel.shouldReOnboard) {
            if (!this.isFinishing && !this.isDestroyed) {
                this.setResult(Activity.RESULT_OK, Intent(this, SetupHouseholdActivity::class.java))
            }
        }
        super.finish()
    }
}