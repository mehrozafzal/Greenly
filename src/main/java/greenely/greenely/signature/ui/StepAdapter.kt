package greenely.greenely.signature.ui

import android.content.Context
import android.database.DataSetObserver
import androidx.fragment.app.FragmentManager
import android.util.Log
import com.stepstone.stepper.Step
import com.stepstone.stepper.adapter.AbstractFragmentStepAdapter
import greenely.greenely.signature.ui.steps.AddressStep
import greenely.greenely.signature.ui.steps.confirmation_step.ConfirmationStep
import greenely.greenely.signature.ui.steps.PersonalNumberStep
import greenely.greenely.signature.ui.steps.SigningStep
import greenely.greenely.tracking.Tracker

//class StepAdapter(
//        context: Context, fragmentManager: androidx.fragment.app.FragmentManager, val tracker: Tracker
//) : AbstractFragmentStepAdapter(fragmentManager, context) {
//
//    override fun getCount(): Int = 4
//
//
//    var previousPosition=-1
//
//    override fun getItemPosition(`object`: Any): Int {
//        return super.getItemPosition(`object`)
//    }
//
//    override fun createStep(position: Int): Step = when (position) {
//        0 -> PersonalNumberStep()
//        1 -> AddressStep()
//        2 -> SigningStep()
//        3 -> ConfirmationStep()
//        else -> throw IndexOutOfBoundsException("No step for $position")
//    }
//
//    override fun findStep(position: Int): Step {
//        if(previousPosition!=position)
//        {
//            previousPosition=position
//            when(position)
//            {
//                0 -> tracker.trackScreen("PoA PNO and Meter ID")
//                1 -> tracker.trackScreen("PoA Verify Address")
//            }
//
//        }
//
//
//        return super.findStep(position)
//
//    }
//
//
//}

