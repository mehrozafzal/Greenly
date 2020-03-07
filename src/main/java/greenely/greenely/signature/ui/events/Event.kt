package greenely.greenely.signature.ui.events

sealed class Event {
    object Exit : Event()
    object Done : Event()
    object ReadPoa: Event()
    object ReadCombinedPOA: Event()

}
