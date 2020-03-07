package greenely.greenely.settings.ui

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableBoolean
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import dagger.android.support.AndroidSupportInjection
import greenely.greenely.R
import greenely.greenely.api.models.ErrorMessage
import greenely.greenely.databinding.NotificationSettingsFragmentBinding
import greenely.greenely.databinding.RetailTcDialogLayoutBinding
import greenely.greenely.databinding.SingleButtonDialogBinding
import greenely.greenely.extensions.onPropertyChanged
import greenely.greenely.home.ui.HomeFragment
import greenely.greenely.home.util.IMainAcitvityListener
import greenely.greenely.main.router.Route
import greenely.greenely.tracking.Tracker
import greenely.greenely.tracking.TrackerFactory
import kotlinx.android.synthetic.main.notification_settings_fragment.*
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class NotificationSettingsFragment : Fragment() {

    @Inject
    internal lateinit var factory: ViewModelProvider.Factory

    @Inject
    internal lateinit var tracker: Tracker


    private lateinit var viewModel: SettingsViewModel

    private lateinit var binding: NotificationSettingsFragmentBinding


    val activityCallBack: IMainAcitvityListener by lazy {
        activity as IMainAcitvityListener
    }


    /**
     * Create the view and the binding.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.notification_settings_fragment, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(activity!!, factory).get(SettingsViewModel::class.java)
        binding.viewModel = viewModel


        weeklyReportLabel.setOnClickListener {
            weeklyReportToggle.performClick()

        }

        monthlyReportLabel.setOnClickListener {
            monthlyReportToggle.performClick()

        }



        viewModel.onAllNotificationToggleClickListener = CompoundButton.OnCheckedChangeListener { compoundButton: CompoundButton, b: Boolean ->
            if (viewModel.isRetailCustomer()) {
                if (!b) disableRetailToggles(context)
                else enableRetailToggles(context)
            }
            viewModel.onAllNotificationToggleClick(b)


        }

        setRetailToggles()


//        This is only for tracking the clicks correctly
        allSettingToggle.setOnClickListener {
            setTrackingForToggle(allSettingToggle, allSettingToggle.isChecked)
        }

        weeklyReportToggle.setOnClickListener {
            setTrackingForToggle(weeklyReportToggle, weeklyReportToggle.isChecked)
        }

        monthlyReportToggle.setOnClickListener {
            setTrackingForToggle(monthlyReportToggle, monthlyReportToggle.isChecked)
        }

        dailyPricePushToggle.setOnClickListener {
            setTrackingRetailSettingChange(dailyPricePushToggle)
        }

        newOverdueInvoiceToggle.setOnClickListener {
            setTrackingRetailSettingChange(newOverdueInvoiceToggle)
        }

        pricePushToggle.setOnClickListener {
            setTrackingRetailSettingChange(pricePushToggle)

        }


        viewModel.commonToggleClickListener = CompoundButton.OnCheckedChangeListener { compoundButton: CompoundButton, b: Boolean ->

        }




        viewModel.onRetailSettingsToggleClickListener = CompoundButton.OnCheckedChangeListener { compoundButton: CompoundButton, b: Boolean ->
            validateRetailSettingChange()
        }

        binding.toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }



        viewModel.getEvents().observe(this, Observer {
            when (it) {
                is UiEvent.ShowError -> showErrorMessage(it.error)
            }
        })

        viewModel.getNotificationSettings().observe(this, Observer {
            it?.let {
                binding.notificationModel = it
                it.initRetailDisplayToggleValue(viewModel.retailStateHandler.isRetailCustomer.get())
                viewModel.onAllNotificationToggleClick(it.isAllNotificationEnabled.get())

                if(!it.isAllNotificationEnabled.get()) disableRetailToggles(context)


                dailyPricePushContainer.setOnClickListener {
                    dailyPricePushToggle.performClick()
                }

                newOverdueInvoiceContainer.setOnClickListener {
                    newOverdueInvoiceToggle.performClick()
                }

                pricePushContainer.setOnClickListener {
                    dailyPricePushToggle.performClick()
                }

            }
        })


    }


    private fun setRetailToggles() {

        if (!viewModel.isRetailCustomer()) {
            disableRetailToggles(context)
        }
    }

    private fun disableRetailToggles(context: Context?) {
        context?.let {
            pricePushToggle.setRetailToggleDisabled(context)
            dailyPricePushToggle.setRetailToggleDisabled(context)
            newOverdueInvoiceToggle.setRetailToggleDisabled(context)
        }

    }

    private fun enableRetailToggles(context: Context?) {
        context?.let {
            pricePushToggle.setRetailToggleEnabled(context)
            dailyPricePushToggle.setRetailToggleEnabled(context)
            newOverdueInvoiceToggle.setRetailToggleEnabled(context)
        }

    }

    var onRetailContainerClickListener = View.OnClickListener {
        validateRetailSettingChange()
    }

    private fun setTrackingRetailSettingChange(compoundButton: CompoundButton) {
        if (isAllowedToChangeRetailSetting())
            setTrackingForToggle(compoundButton, compoundButton.isChecked)
    }

    private fun validateRetailSettingChange(): Boolean {
        var allNotification = viewModel.notificationSettings.value?.isAllNotificationEnabled?.get()
                ?: false
        if (!viewModel.isRetailCustomer()) {
            displayRetailEnableDialog()
            binding.newOverdueInvoiceToggle.isChecked = false
            binding.pricePushToggle.isChecked = false
            binding.dailyPricePushToggle.isChecked = false
            return false


        }
        if (!allNotification) {
            binding.newOverdueInvoiceToggle.isChecked = false
            binding.pricePushToggle.isChecked = false
            binding.dailyPricePushToggle.isChecked = false
            return false
        }

        return true
    }

    private fun isAllowedToChangeRetailSetting(): Boolean {
        var allNotification = viewModel.notificationSettings.value?.isAllNotificationEnabled?.get()
                ?: false
        return (viewModel.isRetailCustomer() && allNotification)
    }


    private fun setTrackingForToggle(compoundButton: CompoundButton, isChecked: Boolean) {
        when (compoundButton.id) {
            R.id.allSettingToggle ->
                trackEvent("clicked_all_notifications", isChecked.getOnOrOff())
            R.id.weeklyReportToggle ->
                trackEvent("clicked_notification_weekly", isChecked.getOnOrOff())
            R.id.monthlyReportToggle ->
                trackEvent("clicked_notification_monthly", isChecked.getOnOrOff())
            R.id.dailyPricePushToggle ->
                trackEvent("clicked_notification_average_price", isChecked.getOnOrOff())
            R.id.pricePushToggle ->
                trackEvent("clicked_notification_price", isChecked.getOnOrOff())
            R.id.newOverdueInvoiceToggle ->
                trackEvent("clicked_notification_invoice", isChecked.getOnOrOff())

        }

    }


    private fun displayRetailEnableDialog() {
        context?.let {
            var builder = AlertDialog.Builder(it, R.style.AlertDialogTheme2_PasswordChangeError)
            val binding = DataBindingUtil.inflate<SingleButtonDialogBinding>(LayoutInflater.from(context), R.layout.single_button_dialog, null, false)
            builder.setView(binding.root)
            var dialog = builder.create()
            dialog.show()
            dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            binding.message.text = context?.getString(R.string.enable_retail_message)
            binding.close.setOnClickListener {
                dialog.dismiss()
            }

            binding.btnContinue.setOnClickListener {
                dialog.dismiss()
                trackEvent("clicked_retail_unlock", "Settings notifications")

                fragmentManager?.popBackStack(
                        SettingsFragment::class.java!!.name,
                        FragmentManager.POP_BACK_STACK_INCLUSIVE)

                activityCallBack.routeTo(Route.RETAIL)

            }

            dialog.window.setGravity(Gravity.CENTER);
            dialog.show();

        }
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

    override fun onDestroy() {
        super.onDestroy()
        viewModel.postNotificationSetting()
    }

    override fun onResume() {
        super.onResume()
        tracker.trackScreen("Settings Notifications")

    }

    private fun trackEvent(name: String, label: String) {
        tracker.track(TrackerFactory().trackingEvent(
                name,
                "Settings",
                label
        ))
    }


    /**
     * Inject before attaching to the [context].
     */
    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }
}

fun Boolean.getOnOrOff(): String = if (this) "ON" else "OFF"

fun SwitchCompat.setRetailToggleDisabled(context: Context) {
    this.trackDrawable = ContextCompat.getDrawable(context, R.drawable.switch_control_disabled)
    this.thumbDrawable = ContextCompat.getDrawable(context, R.drawable.switch_custom_thumb_disabled)
}

fun SwitchCompat.setRetailToggleEnabled(context: Context) {
    this.trackDrawable = ContextCompat.getDrawable(context, R.drawable.switch_control)
    this.thumbDrawable = ContextCompat.getDrawable(context, R.drawable.switch_custom_thumb)
    this.isEnabled=true
    this.refreshDrawableState()
}