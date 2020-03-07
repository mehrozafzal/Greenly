package greenely.greenely.retail.ui

import androidx.lifecycle.ViewModelProvider
import android.content.Context
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dagger.android.support.AndroidSupportInjection
import greenely.greenely.R
import greenely.greenely.databinding.RetailOnboardingDoneBinding
import greenely.greenely.tracking.Tracker
import io.intercom.android.sdk.Intercom
import javax.inject.Inject


class RetailOnboardingDoneFragment : androidx.fragment.app.Fragment() {

    //TODO: move to main activity
    @Inject
    internal lateinit var factory: ViewModelProvider.Factory

    internal lateinit var binding: RetailOnboardingDoneBinding

    @Inject
    lateinit var tracker: Tracker


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = RetailOnboardingDoneBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.toolbar.inflateMenu(R.menu.retail_menu)
        binding.toolbar.setOnMenuItemClickListener {
            if (it.itemId == R.id.intercom) {
                Intercom.client().displayMessenger()
                true
            } else {
                false
            }
        }


        var bottomNavigationView: BottomNavigationView = activity!!.findViewById(R.id.bottomNavigation)
        bottomNavigationView?.visibility=View.VISIBLE


    }

    override fun onResume() {
        super.onResume()
        tracker.trackScreen("Retail Confirmation")
    }
}
