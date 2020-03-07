package greenely.greenely.forgotpassword

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import android.content.Intent
import androidx.databinding.DataBindingUtil
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import dagger.android.AndroidInjection
import greenely.greenely.R
import greenely.greenely.api.models.ErrorMessage
import greenely.greenely.databinding.ForgotPasswordActivityBinding
import greenely.greenely.extensions.asNetworkError
import greenely.greenely.extensions.asUnexpectedError
import greenely.greenely.extensions.hideKeyboard
import greenely.greenely.extensions.setErrorMessage
import greenely.greenely.tracking.Tracker
import greenely.greenely.tracking.TrackerFactory
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

/**
 * Activity used for recovering a lost password
 *
 * @author Anton Holmberg
 */
class ForgotPasswordActivity : AppCompatActivity() {

    @Inject
    internal lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    internal lateinit var tracker: Tracker

    private lateinit var viewModel: ForgotPasswordViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(ForgotPasswordViewModel::class.java)

        val binding: ForgotPasswordActivityBinding =
                DataBindingUtil.setContentView(this, R.layout.forgot_password_activity)
        binding.viewModel = viewModel

        viewModel.error.observe(this, Observer {
            it?.let {
                val builder = AlertDialog.Builder(this)
                when (it) {
                    is HttpException -> {
                        val errorMessage = ErrorMessage.fromError(it)
                        if (errorMessage != null) builder.setErrorMessage(errorMessage)
                        else builder.asUnexpectedError()
                    }
                    is IOException -> builder.asNetworkError()
                    else -> builder.asUnexpectedError()
                }
                builder.setPositiveButton(R.string.okay, { dialog, _ -> dialog.dismiss() })
                        .create()
                        .show()

            }
        })
        viewModel.loading.observe(this, Observer {
            it?.let {
                if (it) {
                    binding.progress.show()
                } else {
                    binding.progress.hide()
                }
            }
        })

        viewModel.events.observe(this, Observer {
            when (it) {
                ForgotPasswordEvent.FINISH -> finish()
                ForgotPasswordEvent.SHOW_HELP -> showHelpDialog()
                ForgotPasswordEvent.SHOW_SUCCESS -> showSuccessDialog()
            }
        })

        binding.progress.viewTreeObserver.addOnGlobalLayoutListener {
            if (binding.progress.visibility == View.VISIBLE) {
                binding.done.visibility = View.GONE
            } else {
                binding.done.visibility = View.VISIBLE
            }
        }

        binding.toolbar.inflateMenu(R.menu.forgot_password_menu)
    }

    private fun showHelpDialog() {
        AlertDialog.Builder(this, R.style.AlertDialogTheme)
                .setTitle(getString(R.string.do_you_need_help))
                .setMessage(getString(R.string.if_you_still_need_help))
                .setPositiveButton(
                        getString(R.string.yes)
                ) { dialog, _ ->
                    startEmailClient()
                    dialog.dismiss()
                }
                .setNegativeButton(getString(R.string.no)) { dialog, _ -> dialog.dismiss() }
                .create()
                .show()
    }

    /**
     * When the activity is finishing.
     */
    override fun finish() {
        hideKeyboard()
        super.finish()
    }

    override fun onResume() {
        super.onResume()
        tracker.trackScreen("Forgot Password")
    }

    private fun showSuccessDialog() {
        trackEvent()
        AlertDialog.Builder(this, R.style.AlertDialogTheme)
                .setTitle(R.string.password_reset_title)
                .setMessage(R.string.password_reset)
                .setPositiveButton(R.string.okay) { dialogInterface, _ ->
                    dialogInterface.dismiss()
                    finish()
                }
                .setOnDismissListener {
                    finish()
                }
                .create()
                .show()
    }

    private fun startEmailClient() {
        val intent = Intent(
                Intent.ACTION_SENDTO,
                Uri.fromParts(
                        "mailto",
                        getString(R.string.support_email), null
                )
        )
        intent.putExtra(Intent.EXTRA_SUBJECT, String.format(
                getString(R.string.help_report_format),
                getString(R.string.forgot_password_title)
        ))
        startActivity(
                Intent.createChooser(
                        intent, getString(R.string.send_email)
                )
        )
    }

    private fun trackEvent() {
        tracker.track(TrackerFactory().trackingEvent("password_recovery_sent", getString(R.string.login_tracking_catagory)))
    }
}
