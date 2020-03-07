package greenely.greenely.settings.ui

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import dagger.android.support.AndroidSupportInjection
import greenely.greenely.R
import greenely.greenely.api.models.ErrorMessage
import greenely.greenely.databinding.HouseholdSettingsFragmentBinding
import greenely.greenely.databinding.SettingsFragmentBinding
import greenely.greenely.settings.data.Household
import greenely.greenely.setuphousehold.ui.SetupHouseholdActivity
import greenely.greenely.tracking.Tracker
import greenely.greenely.tracking.TrackerFactory
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class HouseholdSettingsFragment : Fragment() {

    @Inject
    internal lateinit var factory: ViewModelProvider.Factory

    @Inject
    internal lateinit var tracker: Tracker


    private val viewModel: SettingsViewModel by lazy {
        ViewModelProviders.of(activity!!, factory).get(SettingsViewModel::class.java)
    }

    private lateinit var binding: HouseholdSettingsFragmentBinding


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        viewModel.household.observe(this, Observer { it?.let { setHouseholdInfo(it) } })

        viewModel.isLoadingHousehold().observe(this, Observer { it?.let { setLoading(it) } })

        viewModel.getEvents().observe(this, Observer {
            when (it) {
                is UiEvent.ShowError -> showErrorMessage(it.error)
            }
        })

        binding.changeParameterContainer.setOnClickListener {
            showHouseHoldParameterChangeDialog()
            trackHouseHoldParameterChangeDialog()
        }

        binding.toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }


    }

    fun trackHouseHoldParameterChangeDialog() {
        tracker.track(
                TrackerFactory().trackingEvent(
                        "clicked_change_parameters",
                        "Settings",
                        "Settings screen"
                ))
    }


    private fun showHouseHoldParameterChangeDialog() {

        AlertDialog.Builder(context)
                .setTitle(R.string.change_household_parameters)
                .setMessage(R.string.reonboarding_change_confirmation)
                .setPositiveButton(R.string.okay) { dialog, _ ->
                    dialog.dismiss()

                    val intent = Intent(activity, SetupHouseholdActivity::class.java)
                    intent.putExtra(SetupHouseholdActivity.SHOULD_REONBOARD, true)
                    activity?.startActivityForResult(
                            intent,
                            SetupHouseholdActivity.COMPLETE_REONBOARDING_ACTIVITY_CODE
                    )
                }
                .setNegativeButton(R.string.no_thank_you) { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
                .show()
    }


    private fun showErrorMessage(error: Throwable) {
        when (error) {
            is HttpException -> {
                ErrorMessage.fromError(error)?.let { errorMessage ->
                    Snackbar.make(binding.mainContainer, errorMessage.description, Snackbar.LENGTH_LONG).show()
                }
            }
            is IOException -> {
                Snackbar.make(binding.mainContainer, R.string.network_error_body, Snackbar.LENGTH_LONG)
                        .show()
            }
            else -> {
                Snackbar.make(binding.mainContainer, R.string.unexpected_error, Snackbar.LENGTH_LONG)
                        .show()
            }
        }
    }

    /**
     * Create the view and the binding.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.household_settings_fragment, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        tracker.trackScreen("Settings Household Info")
        viewModel.getHousehold()


    }


    /**
     * Inject before attaching to the [context].
     */
    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }


    private fun setHouseholdInfo(household: Household) {
        binding.household = household
        binding.invalidateAll()
    }

    private fun setLoading(loading: Boolean) {
        if (loading) {
            binding.progress.show()
            binding.scroll.visibility = View.INVISIBLE
        } else {
            binding.progress.hide()
            if (binding.progress.visibility == View.GONE) {
                binding.scroll.visibility = View.VISIBLE
            }
        }
    }
}