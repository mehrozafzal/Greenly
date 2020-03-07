package greenely.greenely.retail.ui.events

sealed class Event {
    data class LoaderUIEvent(val shouldShow: Boolean) : Event()
    data class VisibilityOfViewUIEvent(val shouldShow: Boolean) : Event()

}
