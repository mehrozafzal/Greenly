package greenely.greenely.splash.ui.navigation

import android.app.Activity
import android.content.Intent
import greenely.greenely.registration.ui.RegistrationActivity

class RegistrationRouteHandler(
        private val activity: Activity,
        var next: NavigationHandler? = null
) : NavigationHandler {

    override fun navigateTo(route: SplashRoute) {
        if (route == SplashRoute.REGISTER) {
            activity.startActivity(Intent(activity, RegistrationActivity::class.java))
        } else {
            next?.navigateTo(route)
        }
    }
}

