package greenely.greenely.setuphousehold.ui

import android.content.Context
import androidx.fragment.app.FragmentManager
import com.stepstone.stepper.Step
import com.stepstone.stepper.adapter.AbstractFragmentStepAdapter
import com.stepstone.stepper.viewmodel.StepViewModel
import greenely.greenely.R
import greenely.greenely.setuphousehold.ui.steps.*

class SetupHouseholdStepAdapter(
        context: Context,
        fragmentManager: androidx.fragment.app.FragmentManager
) : AbstractFragmentStepAdapter(fragmentManager, context) {

    private val steps = listOf<Step>(
            IntroStep(),
            MunicipalityStep(),
            FacilityTypeStep(),
            PrimaryHeatingTypeStep(),
            SecondaryHeatingTypeStep(),
            ConstructionYearStep(),
            ElectricCarsStep(),
            FacilityAreaStep(),
            OccupantsStep()
    )

    override fun getCount(): Int = steps.size

    override fun createStep(position: Int): Step = steps[position]

    override fun getViewModel(position: Int): StepViewModel {
        val builder = StepViewModel.Builder(context)
        when (position) {
            0 -> {
                builder.setBackButtonLabel(context.getString(R.string.cancel_setup_household))
                        .setBackButtonStartDrawableResId(StepViewModel.NULL_DRAWABLE)
            }
        }
        return builder.create()
    }
}

