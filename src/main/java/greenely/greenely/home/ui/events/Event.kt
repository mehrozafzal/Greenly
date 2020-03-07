package greenely.greenely.home.ui.events

import greenely.greenely.main.router.Route

sealed class Event {
    data class StartSignatureNavigationEvent(val requestCode: Int?) : Event()
    data class LoaderUIEvent(val shouldShow: Boolean) : Event()
    data class RouteTo(val route:Route) : Event()


}

