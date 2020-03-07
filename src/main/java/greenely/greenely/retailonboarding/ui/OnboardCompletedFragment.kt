package greenely.greenely.retailonboarding.ui

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dagger.android.support.AndroidSupportInjection
import greenely.greenely.databinding.OnboardCompleteFragmentBinding
import greenely.greenely.signature.OnboardCompleteListener
import greenely.greenely.tracking.Tracker
import javax.inject.Inject

class OnboardCompletedFragment : androidx.fragment.app.Fragment(){

    @Inject
    internal lateinit var tracker: Tracker
    private lateinit var binding:OnboardCompleteFragmentBinding
    private  var onboardCompleteListener: OnboardCompleteListener?=null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = OnboardCompleteFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.doneButton.setOnClickListener {
            onboardCompleteListener?.onboardCompleted()
            activity?.setResult(Activity.RESULT_OK)
            activity?.finish()
        }
    }

    /**
     * Inject when attaching to [context].
     */
    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        onboardCompleteListener = activity as OnboardCompleteListener

        super.onAttach(context)
    }


    override fun onResume() {
        super.onResume()
        tracker.trackScreen("PoA Confirmed")
    }
}