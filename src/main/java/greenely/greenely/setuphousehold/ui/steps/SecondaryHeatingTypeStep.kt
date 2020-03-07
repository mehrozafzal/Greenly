package greenely.greenely.setuphousehold.ui.steps

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import com.stepstone.stepper.Step
import com.stepstone.stepper.VerificationError
import dagger.android.support.AndroidSupportInjection
import greenely.greenely.databinding.SetupHouseholdSecondaryHeatingTypeBinding
import greenely.greenely.setuphousehold.models.HouseholdInputOptions
import greenely.greenely.setuphousehold.ui.SetupHouseholdViewModel
import greenely.greenely.setuphousehold.ui.viewholder.SecondaryHeatingViewHolder
import greenely.greenely.tracking.Tracker
import vn.tiki.noadapter2.OnlyAdapter
import javax.inject.Inject

class SecondaryHeatingTypeStep : androidx.fragment.app.Fragment(), Step {

    private lateinit var binding: SetupHouseholdSecondaryHeatingTypeBinding

    private lateinit var viewModel: SetupHouseholdViewModel

    private lateinit var heatingTypesAdapter: OnlyAdapter

    @Inject
    lateinit var tracker: Tracker

    @Inject
    internal lateinit var factory: ViewModelProvider.Factory

    private lateinit var checkedValuesList: List<Int?>

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = SetupHouseholdSecondaryHeatingTypeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(activity!!, factory)[SetupHouseholdViewModel::class.java]
        binding.viewModel = viewModel

        checkedValuesList =
                listOf(
                        viewModel.householdRequest.secondaryHeatingTypeId.get(),
                        viewModel.householdRequest.tertiaryHeatingTypeId.get(),
                        viewModel.householdRequest.quaternaryHeatingTypeId.get()
                )

        setUpOptions()

    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onSelected() {
        setUpList()
        trackScreen()
    }

    override fun onError(error: VerificationError) {
    }

    override fun verifyStep(): VerificationError? {

        when (viewModel.heatingTypesList.size) {
            0 -> {
                viewModel.householdRequest.secondaryHeatingTypeId.set(null)
                viewModel.householdRequest.tertiaryHeatingTypeId.set(null)
                viewModel.householdRequest.quaternaryHeatingTypeId.set(null)
            }
            1 -> {
                viewModel.householdRequest.secondaryHeatingTypeId.set(viewModel.heatingTypesList[0].id)
                viewModel.householdRequest.tertiaryHeatingTypeId.set(null)
                viewModel.householdRequest.quaternaryHeatingTypeId.set(null)
            }
            2 -> {
                viewModel.householdRequest.secondaryHeatingTypeId.set(viewModel.heatingTypesList[0].id)
                viewModel.householdRequest.tertiaryHeatingTypeId.set(viewModel.heatingTypesList[1].id)
                viewModel.householdRequest.quaternaryHeatingTypeId.set(null)
            }
            3 -> {
                viewModel.householdRequest.secondaryHeatingTypeId.set(viewModel.heatingTypesList[0].id)
                viewModel.householdRequest.tertiaryHeatingTypeId.set(viewModel.heatingTypesList[1].id)
                viewModel.householdRequest.quaternaryHeatingTypeId.set(viewModel.heatingTypesList[2].id)
            }
        }
        return null
    }

    private fun setUpOptions() {

        if (viewModel.shouldReOnboard) {
            //prefill
            if (checkedValuesList.isNotEmpty()) {
                for (i: Int? in checkedValuesList) {
                    if (i != null) {
                        viewModel.heatingTypesList.add(HouseholdInputOptions(i, ""))
                    }
                }
            }
        }

        heatingTypesAdapter = OnlyAdapter.Builder()
                .viewHolderFactory { parent, type ->
                    SecondaryHeatingViewHolder.create(
                            parent,
                            viewModel.heatingTypesList,
                            checkedValuesList
                    )
                }
                .onItemClickListener { view: View, option: Any, _: Int ->
                    if (view is CheckBox && view.isChecked) {
                        //allow checking only 3 options
                        if (option is HouseholdInputOptions && viewModel.heatingTypesList.size < 3) {
                            viewModel.heatingTypesList.add(option)
                        } else {
                            view.isChecked = false
                        }
                    } else {
                        //when unchecking an option, remove it from the checked options list
                        val checkedOption = option as HouseholdInputOptions
                        val iterator = viewModel.heatingTypesList.listIterator()
                        while (iterator.hasNext()) {
                            val currentOption = iterator.next()
                            if (currentOption.id == checkedOption.id) {
                                iterator.remove()
                            }
                        }
                    }

                }
                .build()

        binding.heatingTypes.adapter = heatingTypesAdapter
        binding.heatingTypes.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(
                context,
                androidx.recyclerview.widget.LinearLayoutManager.VERTICAL,
                false
        )

        heatingTypesAdapter.getItemId(0)
    }

    private fun setUpList() {
        viewModel.householdConfigurationOptions.observe(this, Observer {
            it?.let {
                viewModel.heatingTypesList.remove(viewModel.heatingTypesList.find {
                    it.id == viewModel.householdRequest.heatingTypeId.get()
                })
                heatingTypesAdapter.setItems(it.heatingTypes.filter {
                    it.id != viewModel.householdRequest.heatingTypeId.get()
                })
            }
        })
    }

    private fun trackScreen() {
        tracker.trackScreen("Onboarding Secondary Heating")
    }
}
