package greenely.greenely.registration.ui.events

sealed class Event {
    object HideKeyboard : Event()
    object Exit : Event()
}

