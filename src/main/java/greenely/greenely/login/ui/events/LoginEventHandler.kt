package greenely.greenely.login.ui.events

import greenely.greenely.login.ui.LoginActivity
import javax.inject.Inject

class LoginEventHandler @Inject constructor(activity: LoginActivity) : EventHandler {

    private val handler: EventHandler by lazy {
        val exitHandler = ExitHandler(activity)
        val forgotPasswordHandler = ForgotPasswordHandler(activity)

        exitHandler.next = forgotPasswordHandler

        exitHandler
    }

    override fun handleEvent(event: Event) {
        handler.handleEvent(event)
    }
}

