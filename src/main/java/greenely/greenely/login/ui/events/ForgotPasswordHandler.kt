package greenely.greenely.login.ui.events

import android.content.Intent
import greenely.greenely.forgotpassword.ForgotPasswordActivity
import greenely.greenely.login.ui.LoginActivity

class ForgotPasswordHandler(private val activity: LoginActivity, var next: EventHandler? = null) : EventHandler {
    override fun handleEvent(event: Event) {
        if (event is Event.ForgotPassword) {
            activity.startActivity(Intent(activity, ForgotPasswordActivity::class.java))
        } else {
            next?.handleEvent(event)
        }
    }
}
