package greenely.greenely.retailonboarding.broadcast

import android.content.Context
import android.net.ConnectivityManager

object NetworkUtil {
    fun getConnectivityStatus(context: Context): NetworkState {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetworkInfo
        if (activeNetwork != null) {
            if (activeNetwork.type == ConnectivityManager.TYPE_WIFI ||
                    activeNetwork.type == ConnectivityManager.TYPE_MOBILE)
                return NetworkState.CONNECTED
        }
        return NetworkState.DISCONNECTED
    }
}
