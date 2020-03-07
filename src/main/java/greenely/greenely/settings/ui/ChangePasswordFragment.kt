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
import android.view.inputmethod.EditorInfo
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import dagger.android.support.AndroidSupportInjection
import greenely.greenely.R
import greenely.greenely.api.models.ErrorMessage
import greenely.greenely.databinding.ChangePasswordFragmentBinding
import greenely.greenely.databinding.PasswordChangeErrorDialogBinding
import greenely.greenely.databinding.SingleButtonDialogBinding
import greenely.greenely.home.util.IMainAcitvityListener
import greenely.greenely.main.router.Route
import greenely.greenely.tracking.Tracker
import greenely.greenely.tracking.TrackerFactory
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class ChangePasswordFragment: Fragment() {

    @Inject
    internal lateinit var factory: ViewModelProvider.Factory

    @Inject
    internal lateinit var tracker: Tracker


    private lateinit var viewModel: SettingsViewModel

    private lateinit var binding: ChangePasswordFragmentBinding




    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!, factory).get(SettingsViewModel::class.java)

        viewModel.getEvents().observe(this, Observer {
            when (it) {
                is UiEvent.PasswordChanged -> {
                    Snackbar.make(binding.container, R.string.password_changed, Snackbar.LENGTH_LONG)
                            .show()

                    fragmentManager?.
                            popBackStack(
                                    SettingsFragment::class.java.name,
                                    FragmentManager.POP_BACK_STACK_INCLUSIVE)


                }
                is UiEvent.ShowError -> showErrorMessage(it.error)
            }
        })


        binding.btnSave.setOnClickListener {
            validatePassword()
        }

        binding.repeatPassword.setOnEditorActionListener { v, actionId, event ->
            if(actionId==EditorInfo.IME_ACTION_DONE) {
                validatePassword()
                return@setOnEditorActionListener false
            }
            return@setOnEditorActionListener false

        }

        binding.toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        tracker.trackScreen("Settings Change Password")


    }

    /**
     * Inject before attaching to the [context].
     */
    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }


    private fun showErrorMessage(error: Throwable) {
        when (error) {
            is HttpException -> {
                ErrorMessage.fromError(error)?.let { errorMessage ->
                    Snackbar.make(binding.container, errorMessage.description, Snackbar.LENGTH_LONG).show()
                }
            }
            is IOException -> {
                Snackbar.make(binding.container, R.string.network_error_body, Snackbar.LENGTH_LONG)
                        .show()
            }
            else -> {
                Snackbar.make(binding.container, R.string.unexpected_error, Snackbar.LENGTH_LONG)
                        .show()
            }
        }
    }

    /**
     * Create the view and the binding.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.change_password_fragment, container, false)
        return binding.root
    }


    fun validatePassword() {

        trackEvent("clicked_change_password", "Settings Change Password Screen")

        var newPassword=binding.repeatPassword.text.toString()
        var confirmPassword=binding.newPassword.text.toString()
        if(!newPassword.equals(confirmPassword)) {
            displayValidationErrorDialog()
        }
        else {
            viewModel.changePassword(
                    binding.oldPassword.text.toString(),
                    binding.newPassword.text.toString()
            )

        }

    }


    private fun trackEvent(name: String, label: String) {
        tracker.track(TrackerFactory().trackingEvent(
                name,
                "Settings Notifications",
                label

        ))
    }

    private fun displayValidationErrorDialog() {
        context?.let {
            var builder = AlertDialog.Builder(it, R.style.AlertDialogTheme2_PasswordChangeError)
            val dialogbinding = DataBindingUtil.inflate<PasswordChangeErrorDialogBinding>(LayoutInflater.from(context), R.layout.password_change_error_dialog, null, false)
            builder.setView(dialogbinding.root)
            var dialog = builder.create()
            dialog.show()
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialogbinding.title.text = context?.getString(R.string.password_missmatch_title)
            dialogbinding.message.text = context?.getString(R.string.password_missmatch_message)
            dialogbinding.btnContinue.text="Ok"
            dialogbinding.title.visibility=View.VISIBLE
            dialogbinding.btnContinue.setOnClickListener {
                dialog.dismiss()
            }
            dialog.window?.setGravity(Gravity.CENTER);
            dialog.show()


        }
    }




}