package greenely.greenely.login.ui.events

sealed class Event {
    object ForgotPassword : Event()
    object Exit : Event()
}