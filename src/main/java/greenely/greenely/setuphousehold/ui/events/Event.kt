package greenely.greenely.setuphousehold.ui.events

sealed class Event {
    object Abort : Event()
    data class ShowLoader(val shouldShow: Boolean) : Event()
}
