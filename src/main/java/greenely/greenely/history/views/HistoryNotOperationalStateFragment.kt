package greenely.greenely.history.views

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import dagger.android.support.AndroidSupportInjection
import greenely.greenely.R
import greenely.greenely.databinding.HistoryNoContentFragmentBinding
import greenely.greenely.databinding.HistoryNotOperationalStateFragmentBinding
import greenely.greenely.history.HistoryViewModel
import greenely.greenely.history.models.HeaderModel
import javax.inject.Inject

class HistoryNotOperationalStateFragment : androidx.fragment.app.Fragment() {


    private lateinit var binding: HistoryNotOperationalStateFragmentBinding

    @Inject
    internal lateinit var viewModelFactory: ViewModelProvider.Factory


    /**
     * Create the view and the binding.
     */
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil
                .inflate(inflater, R.layout.history_not_operational_state_fragment, container, false)
        return binding.root
    }

    /**
     * Bind the view model.
     */
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val viewModel = ViewModelProviders
                .of(activity!!, viewModelFactory)
                .get(HistoryViewModel::class.java)

        binding.viewModel = viewModel
    }

    /**
     * Inject when attaching to a [context].
     */
    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }
}