package greenely.greenely.history.views

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import androidx.databinding.DataBindingUtil
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dagger.android.support.AndroidSupportInjection
import greenely.greenely.R
import greenely.greenely.databinding.HistoryNoContentFragmentBinding
import greenely.greenely.history.HistoryViewModel
import greenely.greenely.history.models.HeaderModel
import org.joda.time.DateTime
import javax.inject.Inject

/**
 * Fragment for showing no content.
 */
class NoContentFragment : androidx.fragment.app.Fragment() {


    companion object {
        /**
         * Create a chart list fragment for a specified [date].
         */
        fun create( headerModel: HeaderModel): NoContentFragment =
                NoContentFragment().apply {
                    this.headerModel=headerModel
                }
    }

    private var date: DateTime? = null


    private lateinit var binding: HistoryNoContentFragmentBinding

    @Inject
    internal lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var headerModel: HeaderModel


    /**
     * Create the view and the binding.
     */
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil
                .inflate(inflater, R.layout.history_no_content_fragment, container, false)
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
        binding.headerModel=headerModel
    }

    /**
     * Inject when attaching to a [context].
     */
    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }
}

