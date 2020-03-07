package greenely.greenely.setuphousehold.ui.steps

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.stepstone.stepper.Step
import com.stepstone.stepper.VerificationError
import dagger.android.support.AndroidSupportInjection
import greenely.greenely.databinding.SetupHouseholdConstructionYearBinding
import greenely.greenely.errors.exceptions.VerificationException
import greenely.greenely.setuphousehold.ui.SetupHouseholdErrorHandler
import greenely.greenely.setuphousehold.ui.SetupHouseholdViewModel
import greenely.greenely.setuphousehold.ui.setInputOptions
import greenely.greenely.tracking.Tracker
import javax.inject.Inject

class ConstructionYearStep : androidx.fragment.app.Fragment(), Step {

    private lateinit var binding: SetupHouseholdConstructionYearBinding
    private lateinit var viewModel: SetupHouseholdViewModel

    @Inject
    internal lateinit var factory: ViewModelProvider.Factory

    @Inject
    internal lateinit var errorHandler: SetupHouseholdErrorHandler

    @Inject
    lateinit var tracker: Tracker

    var checkedOption: Int = -1

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = SetupHouseholdConstructionYearBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(activity!!, factory)[SetupHouseholdViewModel::class.java]
        binding.viewModel = viewModel

        setUpOptions()
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onSelected() {
        trackScreen()
    }

    override fun onError(error: VerificationError) {
        errorHandler.handleError(VerificationException(error))
    }

    override fun verifyStep(): VerificationError? {
        return if (viewModel.householdRequest.constructionYearId.get() == null) {
            VerificationError(getString(greenely.greenely.R.string.construction_year_error))
        } else null
    }

    private fun setUpOptions() {
        viewModel.householdConfigurationOptions.observe(this, Observer {
            it?.let {

                if (viewModel.shouldReOnboard) {
                    //prefill
                    if (viewModel.householdRequest.constructionYearId.get() != null && viewModel.householdRequest.constructionYearId.get()!! > -1) {
                        checkedOption = viewModel.householdRequest.constructionYearId.get()!!
                    }
                }

                binding.radioGroup.setInputOptions(it.constructionYears, checkedOption)
            }
        })

        binding.radioGroup.setOnCheckedChangeListener { _, i ->
            if (i > -1) viewModel.householdRequest.constructionYearId.set(i)
        }
    }

    private fun trackScreen() {
        tracker.trackScreen("Onboarding Building Year")
    }

}


