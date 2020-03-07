package greenely.greenely.solaranalysis.ui.householdinfo

import android.content.Context
import androidx.fragment.app.FragmentManager
import com.stepstone.stepper.Step
import com.stepstone.stepper.adapter.AbstractFragmentStepAdapter
import greenely.greenely.solaranalysis.ui.householdinfo.steps.*

class SolarAnalysisStepAdapter(
        context: Context,
        fragmentManager: FragmentManager
) : AbstractFragmentStepAdapter(fragmentManager, context) {

    private val steps = listOf<Step>(
            AddressStep(),
            RoofSizeStep(),
            RoofAngleStep(),
            RoofDirectionStep(),
            ResultStep(),
            ContactInfoStep()
    )

    override fun getCount(): Int = steps.size

    override fun createStep(position: Int): Step = steps[position]
}

