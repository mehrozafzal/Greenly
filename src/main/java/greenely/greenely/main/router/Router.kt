package greenely.greenely.main.router

import android.os.Parcel
import android.os.Parcelable

typealias RouteSelectedListener = (route: Route) -> Unit

interface Router {
    companion object {
        val STATE_KEY = "mainFragmentRouterState"
    }

    val currentRoute: Route
    var state: RouterState
    var onRouteSelectedListener: RouteSelectedListener?
    fun routeTo(route: Route)

    data class RouterState(var currentRoute: Route) : Parcelable {

        constructor(parcel: Parcel) : this(currentRoute = Route.valueOf(parcel.readString()))

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(currentRoute.name)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<RouterState> {
            override fun createFromParcel(parcel: Parcel): RouterState {
                return RouterState(parcel)
            }

            override fun newArray(size: Int): Array<RouterState?> {
                return arrayOfNulls(size)
            }
        }
    }
}

